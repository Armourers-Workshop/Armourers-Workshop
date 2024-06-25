package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV2;
import moe.plushie.armourers_workshop.core.skin.exception.SkinSaveException;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkinDocumentExporter {

    private final SkinDocument document;

    private SkinItemTransforms itemTransforms;
    private final HashMap<String, Skin> skins = new HashMap<>();

    public SkinDocumentExporter(SkinDocument document) {
        this.document = document;
    }

    public Skin execute(Player player) throws TranslatableException {
        var skinType = document.getType().getSkinType();
        var settings = new SkinSettings();
        var properties = document.getProperties().copy();
        var parts = convertToParts(document.getRoot());
        var animations = convertToAnimations(document.getAnimations());

        if (parts.isEmpty()) {
            throw SkinSaveException.Type.NO_DATA.build("noting");
        }

//        for (SkinPart part : parts) {
//            ISkinPartType partType = part.getType();
//            Collection<SkinMarker> markers = part.getMarkers();
//            if (partType.getMinimumMarkersNeeded() > markers.size()) {
//                throw SkinSaveException.Type.MARKER_ERROR.build("missingMarker", TranslateUtils.Name.of(partType));
//            }
//            if (markers.size() > partType.getMaximumMarkersNeeded()) {
//                throw SkinSaveException.Type.MARKER_ERROR.build("tooManyMarkers", TranslateUtils.Name.of(partType));
//            }
//        }

        if (skinType == SkinTypes.BLOCK) {
            var boxes = SkinDocumentCollider.generateCollisionBox(document.getRoot());
            settings.setCollisionBox(new ArrayList<>(boxes.values()));

            // check if the skin is not a seat and a bed.
            if (properties.get(SkinProperty.BLOCK_BED) && properties.get(SkinProperty.BLOCK_SEAT)) {
                throw SkinSaveException.Type.BED_AND_SEAT.build("conflictBedSeat");
            }

            // check if multi-block is valid.
            if (properties.get(SkinProperty.BLOCK_MULTIBLOCK) && !boxes.containsKey(Vector3i.ZERO)) {
                throw SkinSaveException.Type.INVALID_MULTIBLOCK.build("missingMainBlock");
            }
        }

        var builder = new Skin.Builder(skinType);

        settings.setEditable(false);
        settings.setItemTransforms(itemTransforms);

        builder.version(SkinSerializer.Versions.V20);
        builder.settings(settings);
        builder.properties(properties);
        builder.parts(parts);
        builder.animations(animations);

        builder.previewData(null);
        builder.blobs(null);

        return builder.build();
    }

    public void setItemTransforms(SkinItemTransforms itemTransforms) {
        this.itemTransforms = itemTransforms;
    }

    public SkinItemTransforms getItemTransforms() {
        return itemTransforms;
    }

    private ArrayList<SkinPart> convertToParts(SkinDocumentNode parent) throws TranslatableException {
        var allParts = new ArrayList<SkinPart>();
        for (var node : parent.children()) {
            if (!node.isEnabled()) {
                // ignore disabled node.
                continue;
            }
            var skin = loadSkin(node);
            var using = loadSkinParts(skin, node);
            var transform = node.getTransform();
            var parts = convertToParts(node);
            if (using == null && parts.isEmpty()) {
                // ignore empty node.
                continue;
            }
            if (using != null && node.getType() == using.get(0).getType() && parts.isEmpty() && transform.isIdentity()) {
                // using original skin data directly.
                var part = using.get(0);
                var builder = new SkinPart.Builder(node.getType());
                if (!node.isLocked()) {
                    builder.name(node.getName());
                }
                builder.transform(part.getTransform());
                builder.cubes(part.getCubeData());
                builder.markers(loadSkinMarkers(node));
                var newPart = builder.build();
                part.getParts().forEach(newPart::addPart);
                allParts.add(newPart);
                continue;
            }
            // create a new part.
            var builder = new SkinPart.Builder(node.getType());
            if (!node.isLocked()) {
                builder.name(node.getName());
            }
            builder.transform(transform);

            var cubes = new SkinCubesV2();
            builder.cubes(cubes);

            builder.markers(loadSkinMarkers(node));
            builder.properties(null);
            builder.blobs(null);

            var part = builder.build();
            if (using != null) {
                using.forEach(part::addPart);
            }
            parts.forEach(part::addPart);
            allParts.add(part);
        }
        return allParts;
    }

    private ArrayList<SkinAnimation> convertToAnimations(List<SkinDocumentAnimation> importedAnimations) throws TranslatableException {
        var animations = new ArrayList<SkinAnimation>();
        if (importedAnimations == null || importedAnimations.isEmpty()) {
            return animations;
        }
        for (var importedAnimation : importedAnimations) {
            var animation = loadSkinAnimation(importedAnimation);
            if (animation != null) {
                animations.add(animation);
            }
        }
        return animations;
    }

    @Nullable
    private List<SkinPart> loadSkinParts(Skin skin, SkinDocumentNode node) throws TranslatableException {
        if (skin != null) {
            var parts = skin.getParts();
            if (!parts.isEmpty()) {
                return parts;
            }
        }
        return null;
    }

    @Nullable
    private SkinAnimation loadSkinAnimation(SkinDocumentAnimation ref) throws TranslatableException {
        var name = ref.getName();
        var descriptor = ref.getDescriptor();
        if (name.isEmpty() || descriptor.isEmpty()) {
            return null;
        }
        var skin = loadSkin(name, descriptor);
        for (var animation : skin.getAnimations()) {
            if (animation.getName().equals(ref.getName())) {
                return animation;
            }
        }
        return null;
    }

    @Nullable
    private Skin loadSkin(SkinDocumentNode node) throws TranslatableException {
        var descriptor = node.getSkin();
        if (!descriptor.isEmpty()) {
            return loadSkin(node.getName(), descriptor);
        }
        return null;
    }

    private Skin loadSkin(String source, SkinDescriptor descriptor) throws TranslatableException {
        var identifier = descriptor.getIdentifier();
        var skin = skins.get(identifier);
        if (skin != null) {
            return skin;
        }
        skin = SkinLoader.getInstance().loadSkin(identifier);
        if (skin == null) {
            throw new TranslatableException("exception.armourers_workshop.load.notFoundNodePart", identifier, source);
        }
        skins.put(identifier, skin);
        return skin;
    }

    private List<SkinMarker> loadSkinMarkers(SkinDocumentNode node) {
        var markers = new ArrayList<SkinMarker>();
        for (var child : node.children()) {
            if (child.isLocator()) {
                int x = -MathUtils.floor(child.getLocation().getX());
                int y = -MathUtils.floor(child.getLocation().getY());
                int z = MathUtils.floor(child.getLocation().getZ());
                var marker = new SkinMarker((byte) x, (byte) y, (byte) z, (byte) 0);
                markers.add(marker);
            }
        }
        return markers;
    }
}
