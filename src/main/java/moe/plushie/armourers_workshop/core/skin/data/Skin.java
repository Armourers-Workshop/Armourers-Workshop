package moe.plushie.armourers_workshop.core.skin.data;

import moe.plushie.armourers_workshop.core.api.client.render.SkinBipedModel;
import moe.plushie.armourers_workshop.core.api.common.skin.ICube;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkin;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.config.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.core.skin.advanced.AdvancedPart;
import moe.plushie.armourers_workshop.core.skin.cubes.CubeRegistry;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

public class Skin implements ISkin {

    public SkinIdentifier requestId;
    public int serverId = -1;
    public SkinModelTexture skinModelTexture;
    public int paintTextureId;
    private SkinProperties properties;
    private ISkinType skinType;
    private int[] paintData;
    private ArrayList<SkinPart> parts;
    private int lightHash = 0;
    private Map<Integer, VoxelShape> cachedShapes = new HashMap<>();

    private int[] averageR = new int[10];
    private int[] averageG = new int[10];
    private int[] averageB = new int[10];

    public Skin(SkinProperties properties, ISkinType skinType, int[] paintData, ArrayList<SkinPart> skinParts) {
        this.properties = properties;
        this.skinType = skinType;
        this.paintData = null;
        // Check if the paint data has any paint on it.
        if (paintData != null) {
            boolean validPaintData = false;
            for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
                if (paintData[i] >>> 16 != 255) {
                    validPaintData = true;
                    break;
                }
            }
            if (validPaintData) {
                this.paintData = paintData;
            }
        }

        this.parts = skinParts;
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

    @OnlyIn(Dist.CLIENT)
    public VoxelShape getRenderShape(SkinBipedModel<?> model) {
        int key = (model.crouching ? 4 : 0) + (model.riding ? 2 : 0) + (model.young ? 1 : 0);
        VoxelShape shape = cachedShapes.get(key);
        if (shape != null) {
            return shape;
        }
        shape = VoxelShapes.empty();
        for (SkinPart skinPart : parts) {
            VoxelShape shape1 = skinPart.getRenderShape();
            ModelRenderer modelRenderer = model.getModelRenderer(skinPart.getType());
            if (modelRenderer != null) {
                shape1 = shape1.move(modelRenderer.x, modelRenderer.y, modelRenderer.z);
            }
            shape = VoxelShapes.or(shape, shape1);
        }
        shape = shape.optimize();
        cachedShapes.put(key, shape);
        return shape;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public void cleanUpDisplayLists() {
        // TODO: IMP
//        for (int i = 0; i < parts.size(); i++) {
//            parts.get(i).getClientSkinPartData().cleanUpDisplayLists();
//        }
//        if (hasPaintData()) {
//            skinModelTexture.deleteGlTexture();
//        }
    }

    public void blindPaintTexture() {
        // TODO: IMP
//        if (hasPaintData()) {
//            GL11.glBindTexture(GL11.GL_TEXTURE_2D, paintTextureId);
//        }
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
                if (!SkinConfig.isEnableSkinPart(part)) {
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
        return properties.get(SkinProperty.ALL_CUSTOM_NAME);
    }

    public String getAuthorName() {
        return properties.get(SkinProperty.ALL_AUTHOR_NAME);
    }

    public int getTotalCubes() {
        int totalCubes = 0;
        for (int i = 0; i < CubeRegistry.INSTANCE.getTotalCubes(); i++) {
            ICube cube = CubeRegistry.INSTANCE.getCubeFormId((byte) i);
            totalCubes += getTotalOfCubeType(cube);
        }
        return totalCubes;
    }

    public int getTotalOfCubeType(ICube cube) {
        int totalOfCube = 0;
        int cubeId = cube.getId();
        // TODO: IMP
//        for (int i = 0; i < parts.size(); i++) {
//            totalOfCube += parts.get(i).getClientSkinPartData().totalCubesInPart[cubeId];
//        }
        return totalOfCube;
    }

    @Override
    public int hashCode() {
        if (lightHash == 0) {
            String result = this.toString();
            for (SkinPart part : parts) {
                result += part.toString();
            }
            lightHash = result.hashCode();
        }
        return lightHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Skin other = (Skin) obj;
        if (other.lightHash == lightHash)
            return true;
        return false;
    }

    @Override
    public String toString() {
        String returnString = "Skin [properties=" + properties + ", type=" + skinType.getRegistryName();
        if (this.paintData != null) {
            returnString += ", paintData=" + Arrays.hashCode(paintData);
        }
        returnString += "]";
        return returnString;
    }

//    public int getMarkerCount() {
//        int count = 0;
//        for (SkinPart part : parts) {
//            count += part.getMarkers().size();
//        }
//        return count;
//    }

    public void addPaintDataParts() {
        if (hasPaintData()) {
            for (ISkinPartType skinPartType : getType().getParts()) {
                if (!skinPartType.isModelOverridden(getProperties())) {
                    SkinPart dummyPart = (SkinPart) skinPartType.makeDummyPaintPart(paintData);
                    if (dummyPart != null) {
                        parts.add(0, dummyPart);
                    }
                }
            }
        }
    }

    public AdvancedPart getAdvancedPart(int index) {
        // TODO Auto-generated method stub
        return new AdvancedPart(0, "");
    }
}
