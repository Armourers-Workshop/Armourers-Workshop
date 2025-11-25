package moe.plushie.armourers_workshop.core.skin.serializer.document;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.SkinSaveException;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenGameProfile;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SkinDocumentExporter {

    private final SkinDocument document;

    private OpenItemTransforms itemTransforms;
    private final HashMap<String, Skin> skins = new HashMap<>();

    public SkinDocumentExporter(SkinDocument document) {
        this.document = document;
    }

    public Skin execute(Player player, OpenGameProfile profile) throws TranslatableException {
        var skinType = document.type().skinType();
        var settings = new SkinSettings();
        var properties = document.properties().copy();
        var parts = convertToParts(document.root());
        var animations = convertToAnimations(document.animations());

        if (parts.isEmpty()) {
            throw SkinSaveException.Type.NO_DATA.build("noting");
        }

//        for (var part : parts) {
//            var partType = part.getType();
//            Collection<SkinMarker> markers = part.getMarkers();
//            if (partType.getMinimumMarkersNeeded() > markers.size()) {
//                throw SkinSaveException.Type.MARKER_ERROR.build("missingMarker", TranslateUtils.Name.of(partType));
//            }
//            if (markers.size() > partType.getMaximumMarkersNeeded()) {
//                throw SkinSaveException.Type.MARKER_ERROR.build("tooManyMarkers", TranslateUtils.Name.of(partType));
//            }
//        }

        if (skinType == SkinTypes.BLOCK) {
            var boxes = SkinDocumentCollider.generateCollisionBox(document.root());
            settings.setCollisionBox(Collections.compactMap(boxes.values(), OpenRectangle3f::new));

            // check if the skin is not a seat and a bed.
            if (properties.get(SkinProperty.BLOCK_BED) && properties.get(SkinProperty.BLOCK_SEAT)) {
                throw SkinSaveException.Type.BED_AND_SEAT.build("conflictBedSeat");
            }

            // check if multi-block is valid.
            if (properties.get(SkinProperty.BLOCK_MULTIBLOCK) && !boxes.containsKey(OpenVector3i.ZERO)) {
                throw SkinSaveException.Type.INVALID_MULTIBLOCK.build("missingMainBlock");
            }
        }

        properties.put(SkinProperty.ALL_AUTHOR_NAME, profile.name());
        // in the offline server the `player.getStringUUID()` is not real player uuid.
        if (profile.id() != null) {
            properties.put(SkinProperty.ALL_AUTHOR_UUID, profile.id().toString());
        }

        var builder = new Skin.Builder(skinType);

        settings.setEditable(false);
        settings.setItemTransforms(itemTransforms);

        // requires override entity size?
        if (properties.get(SkinProperty.OVERRIDE_ENTITY_SIZE)) {
            var width = properties.get(SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH);
            var height = properties.get(SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT);
            var eyeHeight = properties.get(SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT);
            settings.setCollisionBox(Collections.newList(new OpenRectangle3f(0, eyeHeight, 0, width, height, width)));
        }
        properties.remove(SkinProperty.OVERRIDE_ENTITY_SIZE);
        properties.remove(SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH);
        properties.remove(SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT);
        properties.remove(SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT);

        builder.version(SkinSerializer.Versions.LATEST);
        builder.settings(settings);
        builder.properties(properties);
        builder.parts(parts);
        builder.animations(animations);

        builder.previewData(null);
        builder.blobs(null);

        return builder.build();
    }

    public void setItemTransforms(OpenItemTransforms itemTransforms) {
        this.itemTransforms = itemTransforms;
    }

    public OpenItemTransforms itemTransforms() {
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
            var transform = node.transform();
            var parts = convertToParts(node);
            if (using == null && parts.isEmpty()) {
                // ignore empty node.
                continue;
            }
            if (using != null && node.type() == using.get(0).type() && parts.isEmpty() && transform.isIdentity()) {
                // using original skin data directly.
                var part = using.get(0);
                var builder = new SkinPart.Builder(node.type());
                if (!node.isLocked()) {
                    builder.name(node.name());
                }
                builder.transform(part.transform());
                builder.geometries(part.geometries());
                builder.markers(loadSkinMarkers(node));
                builder.properties(part.properties());
                var newPart = builder.build();
                part.children().forEach(newPart::addPart);
                allParts.add(newPart);
                continue;
            }
            // create a new part.
            var builder = new SkinPart.Builder(node.type());
            if (!node.isLocked()) {
                builder.name(node.name());
            }
            builder.transform(transform);

            builder.markers(loadSkinMarkers(node));
            builder.properties(SkinProperties.EMPTY);
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
            var parts = skin.parts();
            if (!parts.isEmpty()) {
                return parts;
            }
        }
        return null;
    }

    @Nullable
    private SkinAnimation loadSkinAnimation(SkinDocumentAnimation ref) throws TranslatableException {
        var name = ref.name();
        var descriptor = ref.descriptor();
        if (name.isEmpty() || descriptor.isEmpty()) {
            return null;
        }
        var skin = loadSkin(name, descriptor);
        for (var animation : skin.animations()) {
            if (animation.name().equals(ref.name())) {
                return animation;
            }
        }
        return null;
    }

    @Nullable
    private Skin loadSkin(SkinDocumentNode node) throws TranslatableException {
        var descriptor = node.skin();
        if (!descriptor.isEmpty()) {
            return loadSkin(node.name(), descriptor);
        }
        return null;
    }

    private Skin loadSkin(String source, SkinDescriptor descriptor) throws TranslatableException {
        var identifier = descriptor.identifier();
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
                int x = -OpenMath.floori(child.location().x());
                int y = -OpenMath.floori(child.location().y());
                int z = OpenMath.floori(child.location().z());
                var marker = new SkinMarker((byte) x, (byte) y, (byte) z, (byte) 0);
                markers.add(marker);
            }
        }
        return markers;
    }
}
