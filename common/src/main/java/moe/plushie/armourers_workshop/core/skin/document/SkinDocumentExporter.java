package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV2;
import moe.plushie.armourers_workshop.core.skin.exception.TranslatableException;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
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
        List<SkinPart> parts = convertToParts(document.getRoot());
        Skin.Builder builder = new Skin.Builder(document.getType().getSkinType());

        if (parts.isEmpty()) {
            throw new TranslatableException("exception.armourers_workshop.save.noting");
        }

        SkinSettings settings = new SkinSettings();
        SkinProperties properties = document.getProperties().copy();

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
            SkinTransform transform = loadTransform(node);
            ArrayList<SkinPart> parts = convertToParts(node);
            boolean isEmpty = parts.isEmpty() && transform.isIdentity();
            if (using == null && node.getType() == SkinPartTypes.ADVANCED && isEmpty) {
                // ignore empty node.
                continue;
            }
            if (using != null && node.getType() == using.get(0).getType() && isEmpty) {
                // using original skin data directly.
                SkinPart part = using.get(0);
                part.setName(node.getName());
                allParts.add(part);
                loadItemTransforms(skin);
                continue;
            }
            // create a new part.
            SkinPart.Builder builder = new SkinPart.Builder(node.getType());
            builder.name(node.getName());
            builder.transform(transform);

            SkinCubesV2 cubes = new SkinCubesV2();
            builder.cubes(cubes);

            builder.markers(null);
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

    private SkinTransform loadTransform(SkinDocumentNode node) {
        Vector3f location = node.getLocation();
        if (!location.equals(Vector3f.ZERO)) {
            location = location.scaling(16); // meters to block
        }
        return SkinTransform.create(location, node.getRotation(), node.getScale());
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
