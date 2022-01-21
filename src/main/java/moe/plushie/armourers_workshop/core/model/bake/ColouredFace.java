package moe.plushie.armourers_workshop.core.model.bake;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.api.ISkinCube;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.render.other.BakedSkinDye;
import moe.plushie.armourers_workshop.core.render.other.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.renderer.SkinModelRenderer;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColouredFace {

    public final int x;
    public final int y;
    public final int z;

    public final int rgb;
    //    public final byte r;
//    public final byte g;
//    public final byte b;
    public final byte a;

    public final Direction direction;
    public final ISkinPaintType paintType;

    private final byte lodLevel;

    public ISkinCube cube;

    public ColouredFace(int x, int y, int z, int rgb, byte a, byte lodLevel, Direction direction, ISkinCube cube, ISkinPaintType paintType) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.rgb = rgb;
//        this.r = r;
//        this.g = g;
//        this.b = b;
        this.a = a;

        this.paintType = paintType;
        this.direction = direction;
        this.lodLevel = lodLevel;

        this.cube = cube;
    }

    public ColouredFace(int x, int y, int z, byte r, byte g, byte b, byte a, byte lodLevel, Direction direction, ISkinCube cube, ISkinPaintType paintType) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.rgb = (r & 0xff) << 16 | (g & 0xff) << 8 | (b & 0xff);
//        this.r = r;
//        this.g = g;
//        this.b = b;
        this.a = a;

        this.paintType = paintType;
        this.direction = direction;
        this.lodLevel = lodLevel;

        this.cube = cube;
    }

//    public static byte[] getColourFromTexture(byte x, byte y, byte z, byte r, byte g, byte b, byte face, BufferedImage image, ISkinPartTypeTextured skinPartTex, boolean oldImage) {
//        Direction facing = Direction.values()[face];
//
//        Point posBase = skinPartTex.getTextureBasePos();
//        if (oldImage) {
//            posBase = skinPartTex.getTextureSkinPos();
//        }
//
//        int posX = posBase.x;
//        int posY = posBase.y;
//
//        int faceX = 0;
//        int faceY = 0;
//
//        int faceWidth = 0;
//        int faceHeight = 0;
//
//        int faceOffsetX = 0;
//        int faceOffsetY = 0;
//
//        if (skinPartTex.isTextureMirrored()) {
//            // TODO Fix mirror.
//            //x = (byte) (-x - 1);
//            //z = (byte) (-z - 1);
//        }
//
//        switch (facing) {
//            case NORTH:
//                faceX = skinPartTex.getGuideSpace().getDepth();
//                faceY = skinPartTex.getGuideSpace().getDepth();
//
//                faceWidth = skinPartTex.getGuideSpace().getWidth();
//                faceHeight = skinPartTex.getGuideSpace().getHeight();
//
//                faceOffsetX = skinPartTex.getGuideSpace().getX() + faceWidth + x;
//                faceOffsetY = skinPartTex.getGuideSpace().getY() + faceHeight + y;
//                break;
//
//            case EAST:
//                faceX = 0;
//                faceY = skinPartTex.getGuideSpace().getDepth();
//
//                faceWidth = skinPartTex.getGuideSpace().getDepth();
//                faceHeight = skinPartTex.getGuideSpace().getHeight();
//
//                faceOffsetX = skinPartTex.getGuideSpace().getZ() + faceWidth + z;
//                faceOffsetX = faceWidth - faceOffsetX - 1;
//                faceOffsetY = skinPartTex.getGuideSpace().getY() + faceHeight + y;
//                break;
//            case SOUTH:
//                faceX = skinPartTex.getGuideSpace().getDepth() * 2 + skinPartTex.getGuideSpace().getWidth();
//                faceY = skinPartTex.getGuideSpace().getDepth();
//
//                faceWidth = skinPartTex.getGuideSpace().getWidth();
//                faceHeight = skinPartTex.getGuideSpace().getHeight();
//
//                faceOffsetX = faceWidth + skinPartTex.getGuideSpace().getX() + x;
//                faceOffsetX = faceWidth - faceOffsetX - 1;
//                faceOffsetY = skinPartTex.getGuideSpace().getY() + faceHeight + y;
//                break;
//            case WEST:
//                faceX = skinPartTex.getGuideSpace().getDepth() + skinPartTex.getGuideSpace().getWidth();
//                faceY = skinPartTex.getGuideSpace().getDepth();
//
//                faceWidth = skinPartTex.getGuideSpace().getDepth();
//                faceHeight = skinPartTex.getGuideSpace().getHeight();
//
//                faceOffsetX = skinPartTex.getGuideSpace().getZ() + faceWidth + z;
//                faceOffsetY = skinPartTex.getGuideSpace().getY() + faceHeight + y;
//                break;
//            case UP:
//                faceX = skinPartTex.getGuideSpace().getDepth();
//                faceY = 0;
//
//                faceWidth = skinPartTex.getGuideSpace().getWidth();
//                faceHeight = skinPartTex.getGuideSpace().getDepth();
//
//                faceOffsetX = skinPartTex.getGuideSpace().getX() + faceWidth + x;
//                faceOffsetY = skinPartTex.getGuideSpace().getZ() + faceHeight + z;
//                break;
//            case DOWN:
//                faceX = skinPartTex.getGuideSpace().getDepth() + skinPartTex.getGuideSpace().getWidth();
//                faceY = 0;
//
//                faceWidth = skinPartTex.getGuideSpace().getWidth();
//                faceHeight = skinPartTex.getGuideSpace().getDepth();
//
//                faceOffsetX = skinPartTex.getGuideSpace().getX() + faceWidth + x;
//                faceOffsetY = skinPartTex.getGuideSpace().getZ() + faceHeight + z;
//                faceOffsetY = faceHeight - faceOffsetY - 1;
//                break;
//        }
//
//        int srcX = MathHelper.clamp(posX + faceX + faceOffsetX, posX + faceX, posX + faceX + faceWidth);
//        int srcY = MathHelper.clamp(posY + faceY + faceOffsetY, posY + faceY, posY + faceY + faceHeight);
//
//        Point p = new Point(srcX, srcY);
//        //ModLogger.log(skinPartTex.getPartName() + " - " + p);
//        if (p.x >= 0 & p.y >= 0 & p.x < image.getWidth() & p.y < image.getHeight()) {
//            int rgb = image.getRGB(p.x, p.y);
//            return PaintingHelper.intToBytes(rgb);
//        }
//        return new byte[]{r, g, b, 0};
//    }

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
                return new byte[]{r, g, b};
            }
        }
        int average = ((r & 0xFF) + (g & 0xFF) + (b & 0xFF)) / 3;
        int modelAverage = (modelAverageColour[0] + modelAverageColour[1] + modelAverageColour[2]) / 3;
        int nR = average + (dyeColour[0] & 0xFF) - modelAverage;
        int nG = average + (dyeColour[1] & 0xFF) - modelAverage;
        int nB = average + (dyeColour[2] & 0xFF) - modelAverage;
        nR = MathHelper.clamp(nR, 0, 255);
        nG = MathHelper.clamp(nG, 0, 255);
        nB = MathHelper.clamp(nB, 0, 255);
        return new byte[]{(byte) nR, (byte) nG, (byte) nB};
    }

    @OnlyIn(Dist.CLIENT)
    public void renderVertex(BakedSkinPart part, BakedSkinDye dye, MatrixStack matrixStack, IVertexBuilder builder) {
        int rgb = this.rgb;



//        part.getPackedFaces().colorInfo.getColor(paintType);

//        byte r = this.r;
//        byte g = this.g;
//        byte b = this.b;
//        ISkinDye skinDye = renderData.getSkinDye();
//        IExtraColours extraColours = renderData.getExtraColours();
//        int channelIndex = type.getChannelIndex();
//        // TODO Fix SkinModelTexture to work this way.
//        // Dye
//        if (type.getId() >= 1 && type.getId() <= 8) {
//            // Is a dye paint
//            if (skinDye != null && skinDye.haveDyeInSlot(type.getId() - 1)) {
//                byte[] dye = skinDye.getDyeColour(type.getId() - 1);
//                if (dye.length == 4) {
//                    IPaintType dyeType = PaintTypeRegistry.getInstance().getPaintTypeFormByte(dye[3]);
//                    if (dyeType == PaintTypeRegistry.PAINT_TYPE_NONE) {
//                        return;
//                    }
//
//                    if (dyeType == PaintTypeRegistry.PAINT_TYPE_NORMAL) {
//                        //index = dyeType.getChannelIndex();
//                        int[] averageRGB = cspd.getAverageDyeColour(channelIndex);
//                        byte[] dyedColour = null;
//                        if (dyeType.getColourType() != null & extraColours != null) {
//                            byte[] extraColour = extraColours.getColourBytes(dyeType.getColourType());
//                            if (extraColour.length == 4 && (extraColour[3] & 0xFF) != 0) {
//                                dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(dyeType.getColourType()), averageRGB);
//                            } else {
//                                dyedColour = dyeColour(r, g, b, dye, averageRGB);
//                            }
//                        } else {
//                            dyedColour = dyeColour(r, g, b, dye, averageRGB);
//                        }
//                        r = dyedColour[0];
//                        g = dyedColour[1];
//                        b = dyedColour[2];
//                    }
//                    type = dyeType;
//                }
//            }
//        }
//
//        if (paintType == SkinPaintTypes.RAINBOW) {
//            int[] averageRGB = cspd.getAverageDyeColour(channelIndex);
//            byte[] dyedColour = dyeColour(r, g, b, new byte[] { (byte) 127, (byte) 127, (byte) 127 }, averageRGB);
//            r = dyedColour[0];
//            g = dyedColour[1];
//            b = dyedColour[2];
//        }
//        else if (type == SkinPaintTypes.TEXTURE & renderData.getEntityTexture() != null & SkinConfig.getTexturePaintType() != SkinConfig.TexturePaintType.TEXTURE_REPLACE) {
//            if (skinPart.getType() instanceof ISkinPartTypeTextured) {
//                BufferedImage image = TextureHelper.getBufferedImageSkin(renderData.getEntityTexture());
//                if (image != null) {
//                    byte[] dyedColour = getColourFromTexture(x, y, z, r, g, b, face, image, (ISkinPartTypeTextured) skinPart.getType(), false);
//                    r = dyedColour[0];
//                    g = dyedColour[1];
//                    b = dyedColour[2];
//                }
//            }
//        } else if (extraColours != null) {
//            if (type.getColourType() != null) {
//                int[] averageRGB = cspd.getAverageDyeColour(channelIndex);
//                byte[] dyedColour = dyeColour(r, g, b, extraColours.getColourBytes(type.getColourType()), averageRGB);
//                r = dyedColour[0];
//                g = dyedColour[1];
//                b = dyedColour[2];
//            }
//        }

        if (paintType == SkinPaintTypes.TEXTURE) {
            int newColor = dye.getColor(x, y, z, direction, part.getType());
            if ((newColor & 0xFF000000) != 0) {
                rgb = newColor;
            }
        }

        SkinModelRenderer.renderFace(builder, x, y, z, rgb, a, direction, paintType.getU(), paintType.getV());
    }
}
