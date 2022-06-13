package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkin;
import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.utils.Rectangle3i;
import moe.plushie.armourers_workshop.utils.SkinPaintData;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Skin implements ISkin {

    private final static AtomicInteger COUNTER = new AtomicInteger();

    private final SkinProperties properties;
    private final ISkinType skinType;
    private final ArrayList<SkinPart> parts;

    private final int id = COUNTER.incrementAndGet();

    public int serverId = -1;
    public int paintTextureId;
    //    public SkinModelTexture skinModelTexture;
    private SkinPaintData paintData;
    private int lightHash = 0;

    public Skin(SkinProperties properties, ISkinType skinType, SkinPaintData paintData, ArrayList<SkinPart> skinParts) {
        this.properties = properties;
        this.skinType = skinType;
        this.paintData = paintData;
        this.parts = skinParts;
    }

    public int getId() {
        return id;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public HashMap<BlockPos, Rectangle3i> getBlockBounds() {
        if (skinType != SkinTypes.BLOCK) {
            return null;
        }
        HashMap<BlockPos, Rectangle3i> blockBounds = new HashMap<>();
        for (SkinPart part: getParts()) {
            HashMap<BlockPos, Rectangle3i> partBlockBounds = part.getBlockBounds();
            if (partBlockBounds != null) {
                blockBounds.putAll(partBlockBounds);
            }
        }
        return blockBounds;
    }

    public int getModelCount() {
        int count = 0;
        for (int i = 0; i < parts.size(); i++) {
            count += parts.get(i).getModelCount();
        }
        return count;
    }

    public int getPartCount() {
        return parts.size();
    }

    public int lightHash() {
        if (lightHash == 0) {
            lightHash = this.hashCode();
        }
        return lightHash;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ISkinType getType() {
        return skinType;
    }

    @Nullable
    public SkinPaintData getPaintData() {
        return paintData;
    }

    @Override
    public List<SkinPart> getParts() {
        return parts;
    }


    public boolean isModelOverridden(ISkinPartType partType) {
        for (SkinPart part : parts) {
            if (part.getType() == partType) {
                return part.getType().isModelOverridden(properties);
            }
        }
        return false;
    }

    public String getCustomName() {
        return properties.get(SkinProperty.ALL_CUSTOM_NAME);
    }

    public String getAuthorName() {
        return properties.get(SkinProperty.ALL_AUTHOR_NAME);
    }

    public String getFlavourText() {
        return properties.get(SkinProperty.ALL_FLAVOUR_TEXT);
    }

    public int getTotalCubes() {
        int totalCubes = 0;
        for (int i = 0; i < SkinCubes.getTotalCubes(); i++) {
            ISkinCube cube = SkinCubes.byId(i);
            totalCubes += getTotalOfCubeType(cube);
        }
        return totalCubes;
    }

    public int getTotalOfCubeType(ISkinCube cube) {
        int totalOfCube = 0;
        int cubeId = cube.getId();
        // TODO: IMP
//        for (int i = 0; i < parts.size(); i++) {
//            totalOfCube += parts.get(i).getClientSkinPartData().totalCubesInPart[cubeId];
//        }
        return totalOfCube;
    }

    @Override
    public String toString() {
        String returnString = "Skin [properties=" + properties + ", type=" + skinType.getRegistryName();
        if (this.paintData != null) {
            returnString += ", paintData=" + paintData;
        }
        returnString += "]";
        return returnString;
    }

    public Collection<SkinMarker> getMarkers() {
        ArrayList<SkinMarker> markers = new ArrayList<>();
        for (SkinPart part : parts) {
            markers.addAll(part.getMarkers());
        }
        return markers;
    }
}
