package moe.plushie.armourers_workshop.client.model.bake;

import java.awt.Point;
import java.awt.image.BufferedImage;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.skin.ClientSkinPartData;
import moe.plushie.armourers_workshop.common.SkinHelper;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.painting.PaintRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class ColouredFace {

    public final byte x;
    public final byte y;
    public final byte z;

    public final byte r;
    public final byte g;
    public final byte b;
    private final byte a;

    private final byte t;
    public final byte face;
    private final byte lodLevel;

    public ColouredFace(byte x, byte y, byte z, byte r, byte g, byte b, byte a, byte paintType, byte face, byte lodLevel) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        this.t = paintType;
        this.face = face;
        this.lodLevel = lodLevel;
    }

    public void renderVertex(IRenderBuffer renderBuffer, SkinPartRenderData renderData, ClientSkinPartData cspd) {
        byte r = this.r;
        byte g = this.g;
        byte b = this.b;
        PaintType type = PaintRegistry.getPaintTypeFormByte(t);
        ISkinDye skinDye = renderData.getSkinDye();
        ExtraColours extraColours = renderData.getExtraColours();
        if (type == PaintRegistry.PAINT_TYPE_NONE) {
            return;
        }
        int channelIndex = type.getChannelIndex();
        // Dye
        if (type.getId() >= 1 && type.getId() <= 8) {
            // Is a dye paint
            if (skinDye != null && skinDye.haveDyeInSlot(type.getId() - 1)) {
                byte[] dye = skinDye.getDyeColour(type.getId() - 1);
                if (dye.length == 4) {
                    PaintType dyeType = PaintRegistry.getPaintTypeFormByte(dye[3]);
                    if (dyeType == PaintRegistry.PAINT_TYPE_NONE) {
                        return;
                    }
                    
                    if (dyeType == PaintRegistry.PAINT_TYPE_NORMAL) {
                        //index = dyeType.getChannelIndex();
                        int[] averageRGB = cspd.getAverageDyeColour(channelIndex);
                        byte[] dyedColour = null;
                        if (dyeType.getColourType() != null & extraColours != null) {
                            byte[] extraColour = extraColours.getColourBytes(dyeType.getColourType());
                            if (extraColour.length == 4 && (extraColour[3] & 0xFF) != 0) {
                                dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(dyeType.getColourType()), averageRGB);
                            } else {
                                dyedColour = dyeColour(r, g, b, dye, averageRGB);
                            }
                        } else {
                            dyedColour = dyeColour(r, g, b, dye, averageRGB);
                        }
                        r = dyedColour[0];
                        g = dyedColour[1];
                        b = dyedColour[2];
                    }
                    type = dyeType;
                }
            }
        }
        
        if (type == PaintRegistry.PAINT_TYPE_RAINBOW) {
            int[] averageRGB = cspd.getAverageDyeColour(channelIndex);
            byte[] dyedColour = dyeColour(r, g, b, new byte[] { (byte) 127, (byte) 127, (byte) 127 }, averageRGB);
            r = dyedColour[0];
            g = dyedColour[1];
            b = dyedColour[2];
        } else if (type == PaintRegistry.PAINT_TYPE_TEXTURE & renderData.getEntityTexture() != null) {
            if (renderData.getSkinPart().getPartType() instanceof ISkinPartTypeTextured) {
                BufferedImage image = SkinHelper.getBufferedImageSkin(renderData.getEntityTexture());
                if (image != null) {
                    byte[] dyedColour = getColourFromTexture(x, y, z, r, g, b, face, renderData, image, (ISkinPartTypeTextured) renderData.getSkinPart().getPartType());
                    r = dyedColour[0];
                    g = dyedColour[1];
                    b = dyedColour[2];
                }
            }
        } else if (extraColours != null) {
            if (type.getColourType() != null) {
                int[] averageRGB = cspd.getAverageDyeColour(channelIndex);
                byte[] dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(type.getColourType()), averageRGB);
                r = dyedColour[0];
                g = dyedColour[1];
                b = dyedColour[2];
            }
        }

        // Paint scale 1 / 256.
        double paintScale = 0.00390625D;
        FaceRenderer.renderFace(x, y, z, r, g, b, a, face, lodLevel, type.getU() * paintScale, type.getV() * paintScale, (type.getU() * paintScale) + paintScale, (type.getV() * paintScale) + paintScale);
    }
    
    private static byte[] getColourFromTexture(byte x, byte y, byte z, byte r, byte g, byte b, byte face, SkinPartRenderData renderData, BufferedImage image, ISkinPartTypeTextured skinPartTex) {
        EnumFacing facing = EnumFacing.VALUES[face];
        
        Point posBase = skinPartTex.getTextureBasePos();
        int width = (skinPartTex.getTextureModelSize().getX() * 2) + (skinPartTex.getTextureModelSize().getZ() * 2);
        int height = skinPartTex.getTextureModelSize().getY() + skinPartTex.getTextureModelSize().getZ();
        
        
        int posX = posBase.x;
        int posY = posBase.y;
        
        
        switch (facing) {
        case NORTH:
            posY += skinPartTex.getGuideSpace().getY() + skinPartTex.getGuideSpace().getHeight() + y;
            posX += skinPartTex.getGuideSpace().getX() + skinPartTex.getGuideSpace().getWidth() + x;
            posY += skinPartTex.getTextureModelSize().getZ();
            posX += skinPartTex.getTextureModelSize().getZ();
            break;

        case EAST:
            posY += skinPartTex.getGuideSpace().getY() + skinPartTex.getGuideSpace().getHeight() + y;
            posX += skinPartTex.getGuideSpace().getZ() + skinPartTex.getGuideSpace().getDepth() + z;
            posY += skinPartTex.getTextureModelSize().getZ();
            //posX += skinPartTex.getTextureModelSize().getZ() + skinPartTex.getTextureModelSize().getX();
            break;
        case SOUTH:
            posY += skinPartTex.getGuideSpace().getY() + skinPartTex.getGuideSpace().getHeight() + y;
            posX += skinPartTex.getGuideSpace().getX() + skinPartTex.getGuideSpace().getWidth() + x;
            posY += skinPartTex.getTextureModelSize().getZ();
            posX += skinPartTex.getTextureModelSize().getZ() * 2 + skinPartTex.getTextureModelSize().getX();
            break;
        case WEST:
            posY += skinPartTex.getGuideSpace().getY() + skinPartTex.getGuideSpace().getHeight() + y;
            posX += skinPartTex.getGuideSpace().getZ() + skinPartTex.getGuideSpace().getDepth() + z;
            posY += skinPartTex.getTextureModelSize().getZ();
            posX += skinPartTex.getTextureModelSize().getZ() + skinPartTex.getTextureModelSize().getX();
            break;
        case UP:
            posY += skinPartTex.getGuideSpace().getZ() + skinPartTex.getGuideSpace().getDepth() + z;
            posX += skinPartTex.getGuideSpace().getX() + skinPartTex.getGuideSpace().getWidth() + x;
            posX += skinPartTex.getTextureModelSize().getZ();
            break;
        case DOWN:
            posY += skinPartTex.getGuideSpace().getZ() + skinPartTex.getGuideSpace().getDepth() + z;
            posX += skinPartTex.getGuideSpace().getX() + skinPartTex.getGuideSpace().getWidth() + x;
            posX += skinPartTex.getTextureModelSize().getZ() + skinPartTex.getTextureModelSize().getX();
            break;
        }
        
        //int yOffset = skinPartTex.getGuideSpace().getY() + skinPartTex.getGuideSpace().getHeight();
        //int xOffset = skinPartTex.getGuideSpace().getX() + skinPartTex.getGuideSpace().getWidth();

        Point p = new Point(posX, posY);
        //ModLogger.log(skinPartTex.getPartName() + " - " + p);
        if (p.x >= 0 & p.y >= 0 & p.x < image.getWidth() & p.y < image.getHeight()) {
            int rgb = image.getRGB(p.x, p.y);
            return PaintingHelper.intToBytes(rgb);
        }
        return new byte[] { r, g, b };
    }

    /**
     * Create a new colour for a dyed vertex.
     * 
     * @param dyeColour          RGB byte array.
     * @param modelAverageColour RGB int array.
     * @return
     */
    public static byte[] dyeColour(byte r, byte g, byte b, byte[] dyeColour, int[] modelAverageColour) {
        if (dyeColour.length == 4) {
            if ((dyeColour[3] & 0xFF) == 0) {
                return new byte[] { r, g, b };
            }
        }
        int average = ((r & 0xFF) + (g & 0xFF) + (b & 0xFF)) / 3;
        int modelAverage = (modelAverageColour[0] + modelAverageColour[1] + modelAverageColour[2]) / 3;
        int nR = (int) (average + (dyeColour[0] & 0xFF) - modelAverage);
        int nG = (int) (average + (dyeColour[1] & 0xFF) - modelAverage);
        int nB = (int) (average + (dyeColour[2] & 0xFF) - modelAverage);
        nR = MathHelper.clamp(nR, 0, 255);
        nG = MathHelper.clamp(nG, 0, 255);
        nB = MathHelper.clamp(nB, 0, 255);
        return new byte[] { (byte) nR, (byte) nG, (byte) nB };
    }
}
