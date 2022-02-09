//package moe.plushie.armourers_workshop.core.config.skin;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
//import moe.plushie.armourers_workshop.core.bake.ColouredFace;
//import moe.plushie.armourers_workshop.core.skin.data.Skin;
//import moe.plushie.armourers_workshop.core.skin.data.SkinTexture;
//import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
//import moe.plushie.armourers_workshop.core.utils.BitwiseUtils;
//import net.minecraft.client.renderer.texture.Texture;
//import net.minecraft.client.renderer.texture.TextureUtil;
//import net.minecraft.resources.IResourceManager;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.IntBuffer;
//
//@OnlyIn(Dist.CLIENT)
//public class SkinModelTexture extends Texture {
//
//        private final BufferedImage texture;
////    private final IntBuffer textureBuffer;
//    private final int width = 64;
//    private final int height = 32;
//
////    IntBuffer buffer = IntBuffer.allocate(texture.getWidth() * texture.getHeight());
//
//
//    public SkinModelTexture() {
////        this.textureBuffer = IntBuffer.allocate(width * height);
//        this.texture = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//    }
//
//    public void createTextureForColours(Skin skin, SkinTextureKey cmk) {
////        for (int ix = 0; ix < SkinTexture.TEXTURE_WIDTH; ix++) {
////            for (int iy = 0; iy < SkinTexture.TEXTURE_HEIGHT; iy++) {
////                int paintColour = skin.getPaintData()[ix + (iy * SkinTexture.TEXTURE_WIDTH)];
////                ISkinPaintType paintType = SkinPaintTypes.byId(paintColour >> 24 & 0xff);
////                if (cmk != null) {
////                    if (paintType == SkinPaintTypes.NORMAL) {
////                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
////                    } else if (paintType.getColourType() != null) {
////                        int colour = dyeColour(cmk.getExtraColours().getColourBytes(paintType.getColourType()), paintColour, paintType.getChannelIndex(), skin, cmk);
////                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(colour, 0, 255));
////                    } else if (paintType.getId() >= 1 & paintType.getId() <= 8) {
////                        ISkinDye skinDye = cmk.getSkinDye();
////                        int dyeNumber = paintType.getId() - 1;
////                        if (skinDye.haveDyeInSlot(dyeNumber)) {
////                            byte[] dye = skinDye.getDyeColour(dyeNumber);
////                            ISkinPaintType dyeType = SkinPaintTypes.getInstance().getPaintTypeFormByte(dye[3]);
////                            if (dyeType != SkinPaintTypes.NONE) {
////                                int colour = dyeColour(dye, paintColour, paintType.getChannelIndex(), skin, cmk);
////                                texture.setRGB(ix, iy, colour);
////                            }
////                        } else {
////                            texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
////                        }
////                    }
////                } else {
//                if (paintType == SkinPaintTypes.NORMAL) {
////                    textureBuffer.put(iy * width + ix, paintColour | 0xff000000);
//                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
//                }
//                if (paintType.getId() >= 1 & paintType.getId() <= 8) {
////                    textureBuffer.put(iy * width + ix, paintColour | 0xff000000);
//                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
//                }
////                }
//            }
//        }
//    }
//
//    private int dyeColour(byte[] dye, int colour, int dyeIndex, Skin skin, SkinTextureKey cmk) {
//        byte r = (byte) (colour >>> 16 & 0xFF);
//        byte g = (byte) (colour >>> 8 & 0xFF);
//        byte b = (byte) (colour & 0xFF);
//
//        if (dye.length > 3) {
////            ISkinPaintType t = SkinPaintTypes.getInstance().getPaintTypeFormByte(dye[3]);
////            if (t.getColourType() != null) {
////                //dye = cmk.getExtraColours().getColourBytes(t.getColourType());
////            }
//        }
//
//        int[] average = {127, 127, 127};
//
//        if (skin != null) {
//            average = skin.getAverageDyeColour(dyeIndex);
//        }
//        dye = ColouredFace.dyeColour(r, g, b, dye, average);
//
//        return (255 << 24) + ((dye[0] & 0xFF) << 16) + ((dye[1] & 0xFF) << 8) + (dye[2] & 0xFF);
//    }
//
//    @Override
//    public void load(IResourceManager resourceManager) throws IOException {
//    }
//
//    @Override
//    public void bind() {
//        if (id == -1) {
////            IntBuffer buffer = IntBuffer.allocate(width * texture.getHeight());
////            texture.getRGB(0, 0, texture.getWidth(), texture.getHeight(), buffer.array(), 0, texture.getWidth());
////            TextureUtil.prepareImage(getId(), width, height);
//
//            RenderSystem.activeTexture(33984);
//            RenderSystem.bindTexture(getId());
//
////            IntBuffer buffer = IntBuffer.allocate(width * height);
////            j = bufferedimage.getWidth();
////            k = bufferedimage.getHeight();
//            int[] lvt_8_1_ = new int[width * height];
//            texture.getRGB(0, 0, width, height, lvt_8_1_, 0, width);
////            buffer.flip();
////            buffer.flip();
//            IntBuffer buffer =  ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder()).asIntBuffer();
//            buffer.put(lvt_8_1_);
//            buffer.flip();
//
//
//            TextureUtil.initTexture(buffer, width, height);
//
////        } catch (IOException ioexception) {
////            ioexception.printStackTrace();
////        }
////
////        RenderSystem.activeTexture(33984);
////        RenderSystem.bindTexture(i);
////        TextureUtil.initTexture(intbuffer, j, k);
//
//        }
//        super.bind();
//    }
//}
