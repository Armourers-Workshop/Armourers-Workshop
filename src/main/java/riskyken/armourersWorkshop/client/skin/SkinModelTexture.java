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
import riskyken.armourersWorkshop.client.model.bake.ColouredVertexWithUV;
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
                    if (paintType == 254 | paintType == 253) {
                        //texture.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                    }
                    if (paintType >= 1 & paintType <= 8) {
                        ISkinDye skinDye = cmk.getSkinDye();
                        int dyeNumber = paintType - 1;
                        if (skinDye.haveDyeInSlot(dyeNumber)) {
                            byte[] dye = skinDye.getDyeColour(dyeNumber);
                            
                            byte r = (byte) (paintColour >>> 16 & 0xFF);
                            byte g = (byte) (paintColour >>> 8 & 0xFF);
                            byte b = (byte) (paintColour & 0xFF);
                            
                            int[] average = {127, 127, 127};
                            
                            if (skin != null) {
                                average = skin.getAverageDyeColour(dyeNumber);
                            }
                            
                            dye = ColouredVertexWithUV.dyeColour(r, g, b, dye, average);
                            int colour = (255 << 24) + ((dye[0] & 0xFF) << 16) + ((dye[1] & 0xFF) << 8) + (dye[2]  & 0xFF);
                            texture.setRGB(ix, iy, colour);
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
