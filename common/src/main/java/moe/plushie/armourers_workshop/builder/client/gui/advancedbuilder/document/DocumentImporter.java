package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.common.IResource;
import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.transformer.SkinPack;
import moe.plushie.armourers_workshop.core.skin.transformer.SkinPackReader;
import moe.plushie.armourers_workshop.core.skin.transformer.SkinSerializerV21;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModel;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelBone;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelExporter;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelGeometry;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockModelTexture;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockTransform;
import moe.plushie.armourers_workshop.core.skin.transformer.blockbench.BlockBenchReader;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
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
        String name = file.getName();
        Collection<IResource> resources = SkinSerializerV21.getResourcesFromFile(file);
        return readSkinFromReader(BlockBenchReader.from(name, resources));
    }

    private Skin readSkinFromReader(SkinPackReader reader) throws IOException {
        // access the bedrock addon pack, and the load the entity models.
        CustomModelExporter exporter = new CustomModelExporter();
        reader.loadEntityModel(modelReader -> {
            BedrockModel model = modelReader.readModel();
            for (BedrockModelGeometry geometry : model.getGeometries()) {
                BedrockModelTexture texture = modelReader.readTexture(geometry);
                for (BedrockModelBone bone : geometry.getBones()) {
                    exporter.add(bone, texture);
                }
            }
            SkinPack pack = modelReader.getPack();
            if (pack != null) {
                String name = pack.getName();
                if (name != null && !name.isEmpty()) {
                    exporter.add(SkinProperty.ALL_CUSTOM_NAME, name);
                }
                String description = pack.getDescription();
                if (description != null && !description.isEmpty()) {
                    exporter.add(SkinProperty.ALL_FLAVOUR_TEXT, description);
                }
                Collection<String> authors = pack.getAuthors();
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
            }
            Map<String, BedrockTransform> transforms = modelReader.getTransforms();
            if (transforms != null) {
                transforms.forEach((name, transform) -> {
                    Vector3f translation = transform.getTranslation();
                    Vector3f rotation = transform.getRotation();
                    Vector3f scale = transform.getScale();
                    SkinTransform transform1 = SkinTransform.create(translation, rotation, scale);
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

    public static class CustomModelExporter extends BedrockModelExporter {

        @Override
        protected Skin.Builder createSkin(ISkinType skinType) {
            // we only export advanced skin, because it is an intermediate product.
            Skin.Builder builder = new Skin.Builder(SkinTypes.ADVANCED);

            // note the export skin must to editable.
            SkinSettings settings = new SkinSettings();
            settings.setEditable(true);
            builder.settings(settings);

            return builder;
        }
    }
}
