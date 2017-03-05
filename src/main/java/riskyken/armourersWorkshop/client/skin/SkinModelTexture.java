package riskyken.armourersWorkshop.client.skin;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.model.bake.ColouredFace;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinTexture;
import riskyken.armourersWorkshop.utils.BitwiseUtils;

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
                    if (paintType == 254) {
                        byte[] hairColour = cmk.getExtraColours();
                        int colour = dyeColour(new byte[] {hairColour[3], hairColour[4], hairColour[5]}, paintColour, 9, skin);
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(colour, 0, 255));
                    }
                    if (paintType == 253) {
                        byte[] skinColour = cmk.getExtraColours();
                        int colour = dyeColour(new byte[] {skinColour[0], skinColour[1], skinColour[2]}, paintColour, 8, skin);
                        texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(colour, 0, 255));
                    }
                    if (paintType >= 1 & paintType <= 8) {
                        ISkinDye skinDye = cmk.getSkinDye();
                        int dyeNumber = paintType - 1;
                        if (skinDye.haveDyeInSlot(dyeNumber)) {
                            byte[] dye = skinDye.getDyeColour(dyeNumber);
                            if ((dye[3] & 0xFF) != 0) {
                                int colour = dyeColour(dye, paintColour, dyeNumber, skin);
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
    
    private int dyeColour(int dye, int colour, int dyeIndex, Skin skin) {
        byte[] dyeArray = new byte[3];
        dyeArray[0] = (byte) (dye >>> 16 & 0xFF);
        dyeArray[1] = (byte) (dye >>> 8 & 0xFF);
        dyeArray[2] = (byte) (dye & 0xFF);
        return  dyeColour(dyeArray, colour, dyeIndex, skin);
    }
    
    private int dyeColour(byte[] dye, int colour, int dyeIndex, Skin skin) {
        byte r = (byte) (colour >>> 16 & 0xFF);
        byte g = (byte) (colour >>> 8 & 0xFF);
        byte b = (byte) (colour & 0xFF);
        
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
    }
}
