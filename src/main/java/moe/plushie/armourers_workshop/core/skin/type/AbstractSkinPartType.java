package moe.plushie.armourers_workshop.core.skin.type;

import moe.plushie.armourers_workshop.core.api.common.skin.*;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.data.SkinCubeData;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.SkinTexture;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractSkinPartType implements ISkinPartType {

    protected String registryName;

    protected Rectangle3D buildingSpace;
    protected Rectangle3D guideSpace;
    protected Point3D offset;

    public AbstractSkinPartType() {
    }

    public AbstractSkinPartType setRegistryName(String registryName) {
        this.registryName = registryName;
        return this;
    }

    @Override
    public String getRegistryName() {
        return registryName;
    }

    @Override
    public Collection<ISkinProperty<?>> getProperties() {
        return null;
    }


    @Override
    public Rectangle3D getBuildingSpace() {
        return this.buildingSpace;
    }

    @Override
    public Rectangle3D getGuideSpace() {
        return this.guideSpace;
    }

    @Override
    public Point3D getOffset() {
        return this.offset;
    }

    @Override
    public int getMinimumMarkersNeeded() {
        return 0;
    }

    @Override
    public int getMaximumMarkersNeeded() {
        return 0;
    }

    @Override
    public boolean isPartRequired() {
        return false;
    }

    @Override
    public Point3D getRenderOffset() {
        return new Point3D(0, 0, 0);
    }

    @Override
    public Rectangle3D getItemRenderTextureBounds() {
        return null;
    }


    @Override
    public boolean isModelOverridden(ISkinProperties skinProps) {
        return false;
    }

    @Override
    public boolean isOverlayOverridden(ISkinProperties skinProps) {
        return false;
    }

    @Override
    public SkinPart makeDummyPaintPart(int[] paintData) {
        if (!(this instanceof ISkinPartTypeTextured)) {
            return null;
        }
        BufferedImage image = new BufferedImage(SkinTexture.TEXTURE_WIDTH, SkinTexture.TEXTURE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        for (int ix = 0; ix < SkinTexture.TEXTURE_WIDTH; ix++) {
            for (int iy = 0; iy < SkinTexture.TEXTURE_HEIGHT; iy++) {
                int paintColour = paintData[ix + (iy * SkinTexture.TEXTURE_WIDTH)];
                image.setRGB(ix, iy, paintColour);
            }
        }

        SkinCubeData cubeData = new SkinCubeData();
        cubeData.setCubeCount(guideSpace.getWidth() * guideSpace.getHeight() * guideSpace.getDepth());
        int i = 0;
        for (int ix = 0; ix < guideSpace.getWidth(); ix++) {
            for (int iy = 0; iy < guideSpace.getHeight(); iy++) {
                for (int iz = 0; iz < guideSpace.getDepth(); iz++) {

                    byte x = (byte) (ix - guideSpace.getWidth() - guideSpace.getX());
                    byte y = (byte) (iy - guideSpace.getHeight() - guideSpace.getY());
                    byte z = (byte) (iz - guideSpace.getDepth() - guideSpace.getZ());

                    cubeData.setCubeLocation(i, x, y, z);
//                    for (int side = 0; side < 6; side++) {
//                        byte[] rgbt = ColouredFace.getColourFromTexture(x, y, z, (byte) 0, (byte) 0, (byte) 0, (byte) side, image, (ISkinPartTypeTextured) this, true);
//                        cubeData.setCubeColour(i, side, rgbt[0], rgbt[1], rgbt[2]);
//                        IPaintType paintType = PaintTypeRegistry.getInstance().getPaintTypeFormByte(rgbt[3]);
//                        if (paintType == PaintTypeRegistry.PAINT_TYPE_NONE) {
//                            paintType = PaintTypeRegistry.PAINT_TYPE_NONE;
//                        }
//                        cubeData.setCubePaintType(i, side, (byte) paintType.getId());
//                    }
                    i++;
                }
            }
        }
        return new SkinPart(cubeData, this, new ArrayList<>());
    }
}
