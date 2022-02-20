package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.core.api.ISkinCube;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkin;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Skin implements ISkin {

    private final static AtomicInteger COUNTER = new AtomicInteger();

    private final SkinProperties properties;
    private final ISkinType skinType;
    private final ArrayList<SkinPart> parts;

    private final int id = COUNTER.incrementAndGet();

    public SkinIdentifier requestId;
    public int serverId = -1;
    public int paintTextureId;
    //    public SkinModelTexture skinModelTexture;
    private int[] paintData;
    private int lightHash = 0;
    private int[] averageR = new int[10];
    private int[] averageG = new int[10];
    private int[] averageB = new int[10];

    public Skin(SkinProperties properties, ISkinType skinType, int[] paintData, ArrayList<SkinPart> skinParts) {
        this.properties = properties;
        this.skinType = skinType;
        this.paintData = paintData;
        this.parts = skinParts;
    }

    public int getId() {
        return id;
    }

//    public Rectangle3D getSkinBounds() {
//        int x = 0;
//        int y = 0;
//        int z = 0;
//
//        int width = 1;
//        int height = 1;
//        int depth = 1;
//
//        for (int i = 0; i < parts.size(); i++) {
//            if (!(getType() == SkinTypeRegistry.skinBow && i > 0)) {
//
//                SkinPart skinPart = parts.get(i);
//                Rectangle3D bounds = skinPart.getPartBounds();
//
//                width = Math.max(width, bounds.getWidth());
//                height = Math.max(height, bounds.getHeight());
//                depth = Math.max(depth, bounds.getDepth());
//
//                x = bounds.getX();
//                y = bounds.getY();
//                z = bounds.getZ();
//
//                if (hasPaintData()) {
//                    Rectangle3D skinRec = skinPart.getType().getGuideSpace();
//
//                    width = Math.max(width, skinRec.getWidth());
//                    height = Math.max(height, skinRec.getHeight());
//                    depth = Math.max(depth, skinRec.getDepth());
//
//                    x = Math.max(x, skinRec.getX());
//                    y = Math.max(y, skinRec.getY());
//                    z = Math.max(z, skinRec.getZ());
//                }
//
//            }
//        }
//
//        if (getPartCount() == 0) {
//            for (int i = 0; i < getType().getParts().size(); i++) {
//                ISkinPartType part = getType().getParts().get(i);
//
//                Rectangle3D skinRec = part.getGuideSpace();
//
//                width = Math.max(width, skinRec.getWidth());
//                height = Math.max(height, skinRec.getHeight());
//                depth = Math.max(depth, skinRec.getDepth());
//
//                x = Math.min(x, skinRec.getX());
//                y = Math.max(y, skinRec.getY());
//                z = Math.min(z, skinRec.getZ());
//            }
//        }
//
//        return new Rectangle3D(x, y, z, width, height, depth);
//    }

    public void setAverageDyeValues(int[] r, int[] g, int[] b) {
        this.averageR = r;
        this.averageG = g;
        this.averageB = b;
    }

    public int[] getAverageDyeColour(int dyeNumber) {
        return new int[]{averageR[dyeNumber], averageG[dyeNumber], averageB[dyeNumber]};
    }
//    public VoxelShape getRenderShape() {
//        if (this.cachedShape != null) {
//            return this.cachedShape;
//        }
//        VoxelShape shape = VoxelShapes.empty();
//        for (SkinPart skinPart : parts) {
//            shape = VoxelShapes.or(shape, skinPart.getRenderShape());
//        }
//        this.cachedShape = shape.optimize();
//        return this.cachedShape;
//    }

    public SkinProperties getProperties() {
        return properties;
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

    public boolean hasPaintData() {
        return paintData != null;
    }

    public int[] getPaintData() {
        return paintData;
    }

    @Override
    public List<SkinPart> getParts() {
        return parts;
    }

    public SkinPart getSkinPartFromType(ISkinPartType skinPartType) {
        for (SkinPart part : parts) {
            if (part.getType() == skinPartType) {
                return part;
            }
        }
        return null;
    }

    public boolean isModelOverridden(ISkinPartType partType) {
        for (SkinPart part : parts) {
            if (part.getType() == partType) {
                if (!AWConfig.isEnableSkinPart(part)) {
                    return false;
                }
                return part.getType().isModelOverridden(properties);
            }
        }
        return false;
    }


//    public boolean hasPart(String partRegistryName) {
//        for (SkinPart part : parts) {
//            if (part.getType().getRegistryName().equals(partRegistryName)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public SkinPart getPart(String partRegistryName) {
//        for (SkinPart part : parts) {
//            if (part.getType().getRegistryName().equals(partRegistryName)) {
//                return part;
//            }
//        }
//        return null;
//    }

    public String getCustomName() {
        String name = properties.get(SkinProperty.ALL_CUSTOM_NAME);
        if (!name.isEmpty()) {
            return name;
        }
        return "Skin";
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

//    @Override
//    public int hashCode() {
//        if (lightHash == 0) {
//            String result = this.toString();
//            for (SkinPart part : parts) {
//                result += part.toString();
//            }
//            lightHash = result.hashCode();
//        }
//        return lightHash;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        Skin other = (Skin) obj;
//        if (other.lightHash == lightHash)
//            return true;
//        return false;
//    }

    @Override
    public String toString() {
        String returnString = "Skin [properties=" + properties + ", type=" + skinType.getRegistryName();
        if (this.paintData != null) {
            returnString += ", paintData=" + Arrays.hashCode(paintData);
        }
        returnString += "]";
        return returnString;
    }

    public int getMarkerCount() {
        int count = 0;
        for (SkinPart part : parts) {
            count += part.getMarkers().size();
        }
        return count;
    }

//    public void addPaintDataParts() {
//        if (hasPaintData()) {
//            for (ISkinPartType skinPartType : getType().getParts()) {
//                if (!skinPartType.isModelOverridden(getProperties())) {
//                    SkinPart dummyPart = (SkinPart) skinPartType.makeDummyPaintPart(paintData);
//                    if (dummyPart != null) {
//                        parts.add(0, dummyPart);
//                    }
//                }
//            }
//        }
//    }
//
//    public AdvancedPart getAdvancedPart(int index) {
//        // TODO Auto-generated method stub
//        return new AdvancedPart(0, "");
//    }
}
