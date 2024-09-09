package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.transformer.SkinPackReader;
import moe.plushie.armourers_workshop.core.skin.transformer.SkinSerializerV21;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelExporter;
import moe.plushie.armourers_workshop.core.skin.transformer.blockbench.BlockBenchReader;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class DocumentImporter {

    private boolean keepItemTransforms = false;
    private final File inputFile;
    private final ISkinType skinType;

    public DocumentImporter(File inputFile, ISkinType skinType) {
        this.inputFile = inputFile;
        this.skinType = skinType;
    }

    public boolean isKeepItemTransforms() {
        return keepItemTransforms;
    }

    public void setKeepItemTransforms(boolean keepItemTransforms) {
        this.keepItemTransforms = keepItemTransforms;
    }

    public void execute(Consumer<Skin> consumer) {
        generateSkin((skin, exception) -> {
            try {
                if (skin != null) {
                    consumer.accept(skin);
                } else {
                    throw exception;
                }
            } catch (TranslatableException e) {
                e.printStackTrace();
                NSString message = new NSString(e.getComponent());
                NSString title = NSString.localizedString("advanced-skin-builder.dialog.importer.title");
                UserNotificationCenter.showToast(message, UIColor.RED, title, null);
            } catch (Exception e) {
                e.printStackTrace();
                NSString message = NSString.localizedString("advanced-skin-builder.dialog.importer.unknownException");
                NSString title = NSString.localizedString("advanced-skin-builder.dialog.importer.title");
                UserNotificationCenter.showToast(message, UIColor.RED, title, null);
            }
        });
    }

    private void generateSkin(IResultHandler<Skin> resultHandler) {
        EnvironmentExecutor.runOnBackground(() -> () -> {
            try {
                if (!inputFile.exists()) {
                    throw new TranslatableException("inventory.armourers_workshop.skin-library.error.illegalModelFile");
                }
                Skin skin = readSkinFromFile(inputFile);
                if (skin == null || skin.getParts().isEmpty()) {
                    throw new TranslatableException("inventory.armourers_workshop.skin-library.error.illegalModelFormat");
                }
                Minecraft.getInstance().execute(() -> resultHandler.accept(skin));
            } catch (Exception e) {
                Minecraft.getInstance().execute(() -> resultHandler.throwing(e));
            }
        });
    }

    private Skin readSkinFromFile(File file) throws IOException {
        var name = file.getName();
        var resources = SkinSerializerV21.getResourcesFromFile(file);
        return readSkinFromReader(BlockBenchReader.from(name, resources));
    }

    private Skin readSkinFromReader(SkinPackReader reader) throws IOException {
        // access the bedrock addon pack, and the load the entity models.
        var exporter = new CustomModelExporter();
        reader.setOffset(getOffset());
        reader.loadEntityModel(modelReader -> {
            var model = modelReader.readModel();
            for (var geometry : model.getGeometries()) {
                var texture = modelReader.readTexture(geometry);
                for (var bone : geometry.getBones()) {
                    exporter.add(bone, texture);
                }
            }
            var animations = modelReader.getAnimations();
            if (animations != null) {
                animations.forEach(exporter::add);
            }
            var pack = modelReader.getPack();
            if (pack != null) {
                var name = pack.getName();
                if (name != null && !name.isEmpty()) {
                    exporter.add(SkinProperty.ALL_CUSTOM_NAME, name);
                }
                var description = pack.getDescription();
                if (description != null && !description.isEmpty()) {
                    exporter.add(SkinProperty.ALL_FLAVOUR_TEXT, description);
                }
                var authors = pack.getAuthors();
                if (authors != null && !authors.isEmpty()) {
                    StringBuilder builder = null;
                    for (String a : authors) {
                        if (builder == null) {
                            builder = new StringBuilder(a);
                        } else {
                            builder.append(",");
                            builder.append(a);
                        }
                    }
                    exporter.add(SkinProperty.ALL_AUTHOR_NAME, builder.toString());
                }
                // a special author uuid to identity imported skin.
                exporter.add(SkinProperty.ALL_AUTHOR_UUID, "generated by block bench importer");
            }
            var transforms = modelReader.getTransforms();
            if (transforms != null) {
                transforms.forEach((name, transform) -> {
                    var translation = transform.getTranslation();
                    var rotation = transform.getRotation();
                    var scale = transform.getScale();
                    var transform1 = SkinTransform.create(translation, rotation, scale);
                    exporter.add(name, transform1);
                });
            }
        });

//        exporter.add(SkinProperty.ALL_CUSTOM_NAME, "skin importer");
//        exporter.add(SkinProperty.ALL_FLAVOUR_TEXT, "auto generated by skin importer");
//
//        GameProfile origin = Minecraft.getInstance().getUser().getGameProfile();
//        exporter.add(SkinProperty.ALL_AUTHOR_NAME, "skin importer");
//        if (origin.getId() != null) {
//            exporter.add(SkinProperty.ALL_AUTHOR_UUID, origin.getId().toString());
//        }

//        values.forEach(exporter::add);
        exporter.setKeepItemTransforms(isKeepItemTransforms());
        return exporter.export(skinType);
    }

    private Vector3f getOffset() {
        // work in bedrock_block/bedrock_entity/bedrock_entity_old/geckolib_block/generic_block/modded_entity/optifine_entity
        // not work in java_block, but pack will fix it.
        if (skinType == SkinTypes.BLOCK) {
            return new Vector3f(0, 8, 0);
        }
        return Vector3f.ZERO;
    }

    public static class CustomModelExporter extends BedrockModelExporter {

        @Override
        protected Skin.Builder createSkin(ISkinType skinType) {
            // we only export advanced skin, because it is an intermediate product.
            var builder = new Skin.Builder(SkinTypes.ADVANCED);

            // note the export skin must to editable.
            var settings = new SkinSettings();
            settings.setEditable(true);
            builder.settings(settings);

            return builder;
        }
    }
}
