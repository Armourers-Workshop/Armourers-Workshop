package moe.plushie.armourers_workshop.client.skin;

import java.awt.image.BufferedImage;
import java.io.IOException;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinTexture;
import moe.plushie.armourers_workshop.utils.BitwiseUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinModelTexture extends AbstractTexture {
    
    private final BufferedImage texture;
    
    public SkinModelTexture() {
        this.texture = new BufferedImage(SkinTexture.TEXTURE_WIDTH, SkinTexture.TEXTURE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }
    
    public void createTextureForColours(Skin skin, SkinTextureKey cmk) {
        for (int ix = 0; ix < SkinTexture.TEXTURE_WIDTH; ix++) {
            for (int iy = 0; iy < SkinTexture.TEXTURE_HEIGHT; iy++) {
                int paintColour = skin.getPaintData()[ix + (iy * SkinTexture.TEXTURE_WIDTH)];
                int paintType = BitwiseUtils.getUByteFromInt(paintColour, 0);
                if (cmk != null) {
                    if (paintType == 255) {
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                    }
                    
                    if (paintType == PaintType.HAIR.getKey()) {
                        int colour = dyeColour(cmk.getExtraColours().getColourBytes(ExtraColourType.HAIR), paintColour, 9, skin, cmk);
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(colour, 0, 255));
                    }
                    if (paintType == PaintType.SKIN.getKey()) {
                        int colour = dyeColour(cmk.getExtraColours().getColourBytes(ExtraColourType.SKIN), paintColour, 8, skin, cmk);
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(colour, 0, 255));
                    }
                    if (paintType == PaintType.EYE.getKey()) {
                        int colour = dyeColour(cmk.getExtraColours().getColourBytes(ExtraColourType.EYE), paintColour, 10, skin, cmk);
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(colour, 0, 255));
                    }
                    if (paintType == PaintType.MISC.getKey()) {
                        int colour = dyeColour(cmk.getExtraColours().getColourBytes(ExtraColourType.MISC), paintColour, 11, skin, cmk);
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(colour, 0, 255));
                    }
                    
                    if (paintType >= 1 & paintType <= 8) {
                        ISkinDye skinDye = cmk.getSkinDye();
                        int dyeNumber = paintType - 1;
                        if (skinDye.haveDyeInSlot(dyeNumber)) {
                            byte[] dye = skinDye.getDyeColour(dyeNumber);
                            if ((dye[3] & 0xFF) != 0) {
                                int colour = dyeColour(dye, paintColour, dyeNumber, skin, cmk);
                                texture.setRGB(ix, iy, colour);
                            }
                        } else {
                            texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255)); 
                        }
                    }
                } else {
                    if (paintType == 255) {
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                    }
                    if (paintType >= 1 & paintType <= 8) {
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                    }
                }
            }
        }
    }
    
    private int dyeColour(byte[] dye, int colour, int dyeIndex, Skin skin, SkinTextureKey cmk) {
        byte r = (byte) (colour >>> 16 & 0xFF);
        byte g = (byte) (colour >>> 8 & 0xFF);
        byte b = (byte) (colour & 0xFF);
        
        if (dye.length > 3) {
            byte t = dye[3];
            if ((t & 0xFF) == PaintType.HAIR.getKey()) {
                dye = cmk.getExtraColours().getColourBytes(ExtraColourType.HAIR);
            }
            if ((t & 0xFF) == PaintType.SKIN.getKey()) {
                dye = cmk.getExtraColours().getColourBytes(ExtraColourType.SKIN);
            }
            if ((t & 0xFF) == PaintType.EYE.getKey()) {
                dye = cmk.getExtraColours().getColourBytes(ExtraColourType.EYE);
            }
            if ((t & 0xFF) == PaintType.MISC.getKey()) {
                dye = cmk.getExtraColours().getColourBytes(ExtraColourType.MISC);
            }
        }
        
        int[] average = {127, 127, 127};
        
        if (skin != null) {
            average = skin.getAverageDyeColour(dyeIndex);
        }
        dye = ColouredFace.dyeColour(r, g, b, dye, average);
        
        return (255 << 24) + ((dye[0] & 0xFF) << 16) + ((dye[1] & 0xFF) << 8) + (dye[2]  & 0xFF);
    }
    
    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
    }
    
    public void bindTexture() {
        if (glTextureId == -1) {
            getGlTextureId();
            TextureUtil.uploadTextureImage(glTextureId, texture);
        }
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
        GlStateManager.bindTexture(glTextureId);
    }
}
