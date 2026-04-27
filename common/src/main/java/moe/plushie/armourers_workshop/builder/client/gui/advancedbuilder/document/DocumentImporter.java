package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.core.client.gui.notification.UserNotificationCenter;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench.BlockBenchExporter;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench.BlockBenchPack;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench.BlockBenchPackReader;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class DocumentImporter {

    private boolean keepItemTransforms = false;
    private boolean isAdaptMode = false;

    private final File inputFile;
    private final SkinType targetType;
    private final DocumentPartMapper partMapper;

    public DocumentImporter(File inputFile, SkinType targetType) {
        this.inputFile = inputFile;
        this.targetType = targetType;
        this.partMapper = DocumentPartMapper.of(targetType);
        //this.boneMapper = DocumentBoneMapper.of(SkinTypes.ADVANCED);
    }

    public boolean isAdaptMode() {
        return isAdaptMode;
    }

    public void setAdaptMode(boolean adaptMode) {
        this.isAdaptMode = adaptMode;
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
                var message = new NSString(e.getComponent());
                var title = NSString.localizedString("advanced-skin-builder.dialog.importer.title");
                UserNotificationCenter.showToast(message, UIColor.RED, title, null);
            } catch (Throwable e) {
                e.printStackTrace();
                var message = NSString.localizedString("advanced-skin-builder.dialog.importer.unknownException");
                var title = NSString.localizedString("advanced-skin-builder.dialog.importer.title");
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
                var skin = readSkinFromFile(inputFile);
                if (skin == null || skin.parts().isEmpty()) {
                    throw new TranslatableException("inventory.armourers_workshop.skin-library.error.illegalModelFormat");
                }
                Minecraft.getInstance().execute(() -> resultHandler.accept(apply(skin)));
            } catch (Exception e) {
                Minecraft.getInstance().execute(() -> resultHandler.abort(e));
            }
        });
    }

    private Skin readSkinFromFile(File file) throws IOException {
        return readSkinFromReader(new BlockBenchPackReader(file));
    }

    private Skin readSkinFromReader(BlockBenchPackReader reader) throws IOException {
        var pack = reader.readPack();
        var exporter = new BlockBenchExporter(pack);

        var settings = exporter.settings();
        var properties = exporter.properties();

        var name = pack.name();
        if (name != null && !name.isEmpty()) {
            properties.put(SkinProperty.ALL_CUSTOM_NAME, name);
        }

        var description = pack.description();
        if (description != null && !description.isEmpty()) {
            properties.put(SkinProperty.ALL_FLAVOUR_TEXT, description);
        }

        var authors = pack.authors();
        if (authors != null && !authors.isEmpty()) {
            var joiner = new StringJoiner(",");
            authors.forEach(joiner::add);
            properties.put(SkinProperty.ALL_AUTHOR_NAME, joiner.toString());
        }

        // a special author uuid to identity imported skin.
        properties.put(SkinProperty.ALL_AUTHOR_UUID, "generated by block bench importer");

        // the export skin must to editable.
        settings.setEditable(true);

        exporter.setOffset(getOffset(pack));
        exporter.setDisplayOffset(getDisplayOffset(pack));
        exporter.setCulling(isCulling(pack));

        return exporter.export();
    }

    private Skin apply(Skin skin) {
        var settings = skin.settings().copy();
        var properties = skin.properties().copy();

        if (!isKeepItemTransforms()) {
            settings.setItemTransforms(null);
        }

        var resolvedParts = resolveMappedParts(skin.parts());
        var resolvedAnimations = resolveMappedAnimations(skin.animations());

        var rootParts = new ArrayList<>(resolvedParts);
        if (partMapper.root() != null) {
            // merge into one part
            var rootEntry = partMapper.root();
            var builder = new SkinPart.Builder(rootEntry.type());
            builder.children(resolvedParts);
            rootParts.clear();
            rootParts.add(builder.build());
        } else if (isAdaptMode) {
            // if adapt mode is enabled, we don't need to extract to root part.
            var builder = new SkinPart.Builder(SkinPartTypes.ADVANCED_STATIC);
            builder.children(resolvedParts);
            rootParts.clear();
            rootParts.add(builder.build());
        } else if (!partMapper.isEmpty()) {
            resolvedParts.forEach(it -> extractToRootPart(it, new Stack<>(), rootParts));
        }

        var builder = new Skin.Builder(SkinTypes.ADVANCED);
        builder.parts(rootParts);
        builder.settings(settings);
        builder.properties(properties);
        builder.animations(resolvedAnimations);
        builder.version(SkinSerializer.Versions.LATEST);
        return builder.build();
    }

    public List<SkinPart> resolveMappedParts(List<SkinPart> parts) {
        var results = new ArrayList<SkinPart>();
        for (var part : parts) {
            var node = partMapper.resolve(part.name(), part.type());
            var builder = new SkinPart.Builder(node.type());
            builder.copyFrom(part);
            // change part properties?
            if (isAdaptMode && USE_ADAPT_MODE.contains(node.type())) {
                var newProperties = part.properties().copy();
                newProperties.put(SkinProperty.USE_ADAPT_MODE, true);
                builder.properties(newProperties);
            }
            builder.name(node.name());
            builder.children(resolveMappedParts(part.children()));
            results.add(builder.build());
        }
        return results;
    }

    private List<SkinAnimationData> resolveMappedAnimations(List<SkinAnimationData> animations) {
        var results = new ArrayList<SkinAnimationData>();
        for (var animation : animations) {
            var animators = new ArrayList<SkinAnimationData.Animator>();
            for (var animator : animation.animators()) {
                var node = partMapper.resolve(animator.bone(), SkinPartTypes.ADVANCED);
                animators.add(new SkinAnimationData.Animator(node.name(), animator.options(), animator.keyframes()));
            }
            var name = animation.name();
            var duration = animation.duration();
            var loop = animation.loop();
            results.add(new SkinAnimationData(name, duration, loop, animators));
        }
        return results;
    }

    private void extractToRootPart(SkinPart part, Stack<SkinPart> parent, List<SkinPart> rootParts) {
        // search all child part.
        var children = new ArrayList<>(part.children());
        for (var child : children) {
            parent.push(part);
            extractToRootPart(child, parent, rootParts);
            parent.pop();
        }
        // the part is rewrite?
        var entry = partMapper.get(part.name());
        if (entry != null && entry.isRootPart()) {
            // remove from the part tree.
            if (parent.isEmpty()) {
                rootParts.remove(part);
            } else {
                var parentPart = parent.peek();
                parentPart.removePart(part);
            }
            var builder = new SkinPart.Builder(entry.type());
            builder.copyFrom(part);
            builder.transform(convertToLocal(part, entry, parent));
            rootParts.add(builder.build());
        }
    }

    private OpenTransform3f convertToLocal(SkinPart part, DocumentPartMapper.Entry entry, Stack<SkinPart> parent) {
        // TODO: @SAGESSE add built-in pivot support.
        //var origin = getParentOrigin(parent).adding(transform.getTranslate());
        //translate = origin;
        //rotation = transform.getRotation();
        //pivot = transform.getPivot();
        var translate = entry.offset(); // 0 + offset
        var rotation = OpenVector3f.ZERO; // never use rotation on the built-in part type.
        return OpenTransform3f.create(translate, rotation, OpenVector3f.ONE);
    }

    private boolean isCulling(BlockBenchPack pack) {
        if (pack.format().equals("java_block")) {
            return true;
        }
        return false;
    }

    private OpenVector3f getOffset(BlockBenchPack pack) {
        // relocation the block model origin to the center(8, 8, 8).
        if (ITEM_TYPES.contains(targetType)) {
            // work in java_block.
            if (pack.format().equals("java_block")) {
                return new OpenVector3f(8, 8, 8);
            }
            // work in bedrock_block/bedrock_entity/bedrock_entity_old/geckolib_block/generic_block/modded_entity/optifine_entity.
            return new OpenVector3f(0, 8, 0);
        }
        // relocation the entity model origin to the head bottom (0, 24, 0).
        if (targetType == SkinTypes.OUTFIT || targetType == SkinTypes.ARMOR_HEAD || targetType == SkinTypes.ARMOR_CHEST || targetType == SkinTypes.ARMOR_LEGS || targetType == SkinTypes.ARMOR_FEET || targetType == SkinTypes.ARMOR_WINGS) {
            return new OpenVector3f(0, 24, 0);
        }
        if (targetType == SkinTypes.HORSE) {
            return new OpenVector3f(0, 24, 0);
        }
        return OpenVector3f.ZERO;
    }

    private OpenVector3f getDisplayOffset(BlockBenchPack pack) {
        // the java_block display center is same the wen model center.
        if (pack.format().equals("java_block")) {
            return OpenVector3f.ZERO;
        }
        // work in bedrock_block/bedrock_entity/bedrock_entity_old/geckolib_block/generic_block/modded_entity/optifine_entity.
        return new OpenVector3f(0, 8, 0);
    }


    private OpenVector3f getParentOrigin(Stack<SkinPart> parent) {
        var origin = OpenVector3f.ZERO;
        for (var part : parent) {
            if (part.transform() instanceof OpenTransform3f transform) {
                origin = origin.adding(transform.translate());
            }
        }
        return origin;
    }

    private static final Set<SkinPartType> USE_ADAPT_MODE = Collections.immutableSet(builder -> {
        builder.add(SkinPartTypes.BIPPED_HAT);

        builder.add(SkinPartTypes.BIPPED_HEAD);
        builder.add(SkinPartTypes.BIPPED_CHEST);
        builder.add(SkinPartTypes.BIPPED_LEFT_ARM);
        builder.add(SkinPartTypes.BIPPED_RIGHT_ARM);
        builder.add(SkinPartTypes.BIPPED_SKIRT);
        builder.add(SkinPartTypes.BIPPED_LEFT_THIGH);
        builder.add(SkinPartTypes.BIPPED_RIGHT_THIGH);
        builder.add(SkinPartTypes.BIPPED_LEFT_FOOT);
        builder.add(SkinPartTypes.BIPPED_RIGHT_FOOT);
        builder.add(SkinPartTypes.BIPPED_LEFT_WING);
        builder.add(SkinPartTypes.BIPPED_RIGHT_WING);
        builder.add(SkinPartTypes.BIPPED_LEFT_PHALANX);
        builder.add(SkinPartTypes.BIPPED_RIGHT_PHALANX);

        builder.add(SkinPartTypes.BIPPED_TORSO);
        builder.add(SkinPartTypes.BIPPED_LEFT_HAND);
        builder.add(SkinPartTypes.BIPPED_RIGHT_HAND);
        builder.add(SkinPartTypes.BIPPED_LEFT_LEG);
        builder.add(SkinPartTypes.BIPPED_RIGHT_LEG);

        builder.add(SkinPartTypes.HORSE_HEAD);
        builder.add(SkinPartTypes.HORSE_NECK);
        builder.add(SkinPartTypes.HORSE_CHEST);
        builder.add(SkinPartTypes.HORSE_LEFT_FRONT_THIGH);
        builder.add(SkinPartTypes.HORSE_RIGHT_FRONT_THIGH);
        builder.add(SkinPartTypes.HORSE_LEFT_HIND_THIGH);
        builder.add(SkinPartTypes.HORSE_RIGHT_HIND_THIGH);
        builder.add(SkinPartTypes.HORSE_LEFT_FRONT_LEG);
        builder.add(SkinPartTypes.HORSE_RIGHT_FRONT_LEG);
        builder.add(SkinPartTypes.HORSE_LEFT_HIND_LEG);
        builder.add(SkinPartTypes.HORSE_RIGHT_HIND_LEG);
        builder.add(SkinPartTypes.HORSE_TAIL);

        builder.add(SkinPartTypes.BOAT_BODY);
        builder.add(SkinPartTypes.BOAT_LEFT_PADDLE);
        builder.add(SkinPartTypes.BOAT_RIGHT_PADDLE);

        builder.add(SkinPartTypes.MINECART_BODY);
    });


    private static final Set<SkinType> ITEM_TYPES = Collections.immutableSet(builder -> {
        builder.add(SkinTypes.ITEM_SWORD);
        builder.add(SkinTypes.ITEM_SHIELD);
        builder.add(SkinTypes.ITEM_BOW);
        builder.add(SkinTypes.ITEM_TRIDENT);

        builder.add(SkinTypes.ITEM_PICKAXE);
        builder.add(SkinTypes.ITEM_AXE);
        builder.add(SkinTypes.ITEM_SHOVEL);
        builder.add(SkinTypes.ITEM_HOE);

        builder.add(SkinTypes.ITEM_FISHING);
        builder.add(SkinTypes.ITEM_BACKPACK);

        builder.add(SkinTypes.ITEM);
        builder.add(SkinTypes.BLOCK);
    });
}
