package riskyken.armourersWorkshop.common.skin.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.common.SkinHelper;

/**
 * 
 * @author RiskyKen
 *
 */
public class SkinTexture {
    
    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 32;
    
    private final Minecraft mc;
    private BufferedImage bufferedPlayerImage;
    private BufferedImage bufferedSkinImage;
    private int textureId;
    private int[][] paintData;
    
    public SkinTexture() {
        mc = Minecraft.getMinecraft();
        bufferedSkinImage = new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        textureId = -1;
        paintData = new int[TEXTURE_WIDTH][TEXTURE_HEIGHT];
        updateForResourceLocation(AbstractClientPlayer.locationStevePng);
        updateTexture();
    }
    
    @Override
    protected void finalize() throws Throwable {
        deleteTexture();
        super.finalize();
    }
    
    public void updateForProfile(GameProfile gameProfile) {
        ResourceLocation rl = SkinHelper.getSkinResourceLocation(gameProfile, Type.SKIN);
        updateForResourceLocation(rl);
    }
    
    public void updateForResourceLocation(ResourceLocation resourceLocation) {
        BufferedImage bi = null;
        InputStream inputStream = null;
        
        try {
            inputStream = mc.getResourceManager().getResource(resourceLocation).getInputStream();
            bi = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        if (bi == null) {
            return;
        }
        
        bufferedPlayerImage = SkinHelper.deepCopyBufferedImage(bi);
        
        updateTexture();
    }
    
    public void updatePaintData(int x, int y, int colour) {
        paintData[x][y] = colour;
        updateTexture();
    }
    
    private void updateTexture() {
        applyPlayerToTexture();
        applyPaintToTexture();
        uploadTexture();
    }
    
    private void applyPlayerToTexture() {
        for (int ix = 0; ix < TEXTURE_WIDTH; ix++) {
            for (int iy = 0; iy < TEXTURE_HEIGHT; iy++) {
                bufferedSkinImage.setRGB(ix, iy, bufferedPlayerImage.getRGB(ix, iy));
            }
        }
    }
     
    private void applyPaintToTexture() {
        for (int ix = 0; ix < TEXTURE_WIDTH; ix++) {
            for (int iy = 0; iy < TEXTURE_HEIGHT; iy++) {
                bufferedSkinImage.setRGB(ix, iy, paintData[ix][iy]);
            }
        }
    }
    
    private void deleteTexture() {
        if (textureId != -1) {
            TextureUtil.deleteTexture(textureId);
            textureId = -1;
        }
    }
    
    private void uploadTexture() {
        deleteTexture();
        textureId = TextureUtil.glGenTextures();
        TextureUtil.uploadTextureImage(textureId, bufferedSkinImage);
    }
    
    public void bindTexture() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }
}
