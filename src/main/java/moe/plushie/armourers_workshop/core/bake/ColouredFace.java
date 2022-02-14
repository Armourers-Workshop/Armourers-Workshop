package moe.plushie.armourers_workshop.core.bake;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.api.ISkinCube;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.core.skin.data.Palette;
import moe.plushie.armourers_workshop.core.skin.painting.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.VanillaPacketSplitter;

@OnlyIn(Dist.CLIENT)
public class ColouredFace {

    private static final PaintColor RAINBOW_TARGET = new PaintColor(0xff7f7f7f, SkinPaintTypes.RAINBOW);

    public final int x;
    public final int y;
    public final int z;

    private final Direction direction;
    private final PaintColor color;
    private final ISkinCube cube;

    public ColouredFace(int x, int y, int z, PaintColor color, Direction direction, ISkinCube cube) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.cube = cube;
        this.color = color;
        this.direction = direction;
    }


    public Direction getDirection() {
        return direction;
    }

    public PaintColor getColor() {
        return color;
    }

    public ISkinCube getCube() {
        return cube;
    }

    public ISkinPaintType getPaintType() {
        return color.getPaintType();
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


    @OnlyIn(Dist.CLIENT)
    public void render(BakedSkinPart part, Palette palette, MatrixStack matrixStack, IVertexBuilder builder) {
        PaintColor resolvedColor = resolve(color, palette, part.getColorInfo(), part.getType(), 0);
        if (resolvedColor.getPaintType() == SkinPaintTypes.NONE) {
            return;
        }
        SkinModelRenderer.renderFace(x, y, z, resolvedColor, direction, matrixStack, builder);
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
//
//        if (paintType == SkinPaintTypes.TEXTURE) {
//            int newColor = dye.getColor(x, y, z, direction, part.getType());
//            if ((newColor & 0xFF000000) != 0) {
//                rgb = newColor;
//            }
//        }
//
    }

    private PaintColor dye(PaintColor source, PaintColor destination, Integer average) {
        if (destination.getPaintType() == SkinPaintTypes.NONE) {
            return PaintColor.CLEAR;
        }
        if (average == null) {
            return source;
        }
        int alpha = source.getRGB() & destination.getRGB() & average & 0xff000000;
        if (alpha == 0) {
            return PaintColor.CLEAR;
        }
        int src = (source.getRed() + source.getGreen() + source.getBlue()) / 3;
        int avg = ((average >> 16 & 0xff) + (average >> 8 & 0xff) + (average & 0xff)) / 3;
        int r = MathHelper.clamp(destination.getRed() + src - avg, 0, 255);
        int g = MathHelper.clamp(destination.getGreen() + src - avg, 0, 255);
        int b = MathHelper.clamp(destination.getBlue() + src - avg, 0, 255);
        return new PaintColor(alpha | r << 16 | g << 8 | b, destination.getPaintType());
    }

    private PaintColor resolve(PaintColor paintColor, Palette palette, ColorDescriptor descriptor, ISkinPartType partType, int count) {
        ISkinPaintType paintType = paintColor.getPaintType();
        if (paintType == SkinPaintTypes.NONE) {
            return PaintColor.CLEAR;
        }
        if (paintType == SkinPaintTypes.RAINBOW) {
            return dye(paintColor, RAINBOW_TARGET, descriptor.getAverageColor(paintType));
        }
        if (paintType == SkinPaintTypes.TEXTURE && palette.getTextureReader() != null) {
            PaintColor paintColor1 = palette.getTextureReader().getColor(x, y, z, direction, partType);
            if (paintColor1 != null) {
                return paintColor1;
            }
            return paintColor;
        }
        if (paintType.getDyeType() != null && count < 2) {
            PaintColor paintColor1 = palette.getResolvedColor(paintType);
            if (paintColor1 == null) {
                return paintColor;
            }
            paintColor = dye(paintColor, paintColor1, descriptor.getAverageColor(paintType));
            return resolve(paintColor, palette, descriptor, partType, count + 1);
        }
        return paintColor;
    }
}
