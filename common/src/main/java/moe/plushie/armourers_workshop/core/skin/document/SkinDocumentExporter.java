package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV2;
import moe.plushie.armourers_workshop.core.skin.exception.SkinSaveException;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinDocumentExporter {

    private final SkinDocument document;

    private SkinItemTransforms itemTransforms;
    private final HashMap<String, Skin> skins = new HashMap<>();

    public SkinDocumentExporter(SkinDocument document) {
        this.document = document;
    }

    public Skin execute(Player player) throws TranslatableException {
        ISkinType skinType = document.getType().getSkinType();
        SkinSettings settings = new SkinSettings();
        SkinProperties properties = document.getProperties().copy();
        List<SkinPart> parts = convertToParts(document.getRoot());

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
            Map<Vector3i, Rectangle3i> boxes = SkinDocumentCollider.generateCollisionBox(document.getRoot());
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

        Skin.Builder builder = new Skin.Builder(skinType);

        settings.setEditable(false);
        settings.setItemTransforms(itemTransforms);

        builder.version(SkinSerializer.Versions.V20);
        builder.settings(settings);
        builder.properties(properties);
        builder.parts(parts);

        builder.previewData(null);
        builder.blobs(null);

        return builder.build();
    }

    private ArrayList<SkinPart> convertToParts(SkinDocumentNode parent) throws TranslatableException {
        ArrayList<SkinPart> allParts = new ArrayList<>();
        for (SkinDocumentNode node : parent.children()) {
            if (!node.isEnabled()) {
                // ignore disabled node.
                continue;
            }
            Skin skin = loadSkin(node);
            List<SkinPart> using = loadSkinParts(skin, node);
            SkinTransform transform = node.getTransform();
            ArrayList<SkinPart> parts = convertToParts(node);
            if (using == null && parts.isEmpty()) {
                // ignore empty node.
                continue;
            }
            if (using != null && node.getType() == using.get(0).getType() && parts.isEmpty() && transform.isIdentity()) {
                // using original skin data directly.
                SkinPart part = using.get(0);
                SkinPart.Builder builder = new SkinPart.Builder(node.getType());
                builder.name(node.getName());
                builder.transform(part.getTransform());
                builder.cubes(part.getCubeData());
                builder.markers(loadSkinMarkers(node));
                allParts.add(builder.build());
                loadItemTransforms(skin);
                continue;
            }
            // create a new part.
            SkinPart.Builder builder = new SkinPart.Builder(node.getType());
            builder.name(node.getName());
            builder.transform(transform);

            SkinCubesV2 cubes = new SkinCubesV2();
            builder.cubes(cubes);

            builder.markers(loadSkinMarkers(node));
            builder.properties(null);
            builder.blobs(null);

            SkinPart part = builder.build();
            if (using != null) {
                using.forEach(part::addPart);
                loadItemTransforms(skin);
            }
            parts.forEach(part::addPart);
            allParts.add(part);
        }
        return allParts;
    }

    @Nullable
    private List<SkinPart> loadSkinParts(Skin skin, SkinDocumentNode node) throws TranslatableException {
        if (skin != null) {
            List<SkinPart> parts = skin.getParts();
            if (!parts.isEmpty()) {
                return parts;
            }
        }
        return null;
    }

    @Nullable
    private Skin loadSkin(SkinDocumentNode node) throws TranslatableException {
        SkinDescriptor descriptor = node.getSkin();
        if (!descriptor.isEmpty()) {
            return loadSkin(node, descriptor);
        }
        return null;
    }

    private Skin loadSkin(SkinDocumentNode node, SkinDescriptor descriptor) throws TranslatableException {
        String identifier = descriptor.getIdentifier();
        Skin skin = skins.get(identifier);
        if (skin != null) {
            return skin;
        }
        skin = SkinLoader.getInstance().loadSkin(identifier);
        if (skin == null) {
            throw new TranslatableException("exception.armourers_workshop.load.notFoundNodePart", identifier, node.getName());
        }
        skins.put(identifier, skin);
        return skin;
    }

    private List<SkinMarker> loadSkinMarkers(SkinDocumentNode node) {
        ArrayList<SkinMarker> markers = new ArrayList<>();
        for (SkinDocumentNode child : node.children()) {
            if (child.isLocator()) {
                int x = -MathUtils.floor(child.getLocation().getX() * 16);
                int y = -MathUtils.floor(child.getLocation().getY() * 16);
                int z = MathUtils.floor(child.getLocation().getZ() * 16);
                SkinMarker marker = new SkinMarker((byte) x, (byte) y, (byte) z, (byte) 0);
                markers.add(marker);
            }
        }
        return markers;
    }

    private void loadItemTransforms(Skin skin) {
        SkinItemTransforms partItemTransforms = skin.getSettings().getItemTransforms();
        if (partItemTransforms != null) {
            if (itemTransforms == null) {
                itemTransforms = new SkinItemTransforms();
            }
            itemTransforms.putAll(partItemTransforms);
        }
    }
}
