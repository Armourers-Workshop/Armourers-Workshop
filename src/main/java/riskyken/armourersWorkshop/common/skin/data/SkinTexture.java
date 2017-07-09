package riskyken.armourersWorkshop.common.skin.data;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.utils.BitwiseUtils;

/**
 * 
 * @author RiskyKen
 *
 */
public class SkinTexture {
    
    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 32;
    public static final int TEXTURE_SIZE = TEXTURE_WIDTH * TEXTURE_HEIGHT;
    
    private final Minecraft mc;
    private BufferedImage bufferedPlayerImage;
    private BufferedImage bufferedSkinImage;
    private int lastProfileHash;
    private boolean needsUpdate;
    private int textureId;
    private int[] paintData;
    
    public SkinTexture() {
        mc = Minecraft.getMinecraft();
        bufferedSkinImage = new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        lastProfileHash = -1;
        needsUpdate = true;
        textureId = -1;
        paintData = new int[TEXTURE_SIZE];
    }
    
    @Override
    protected void finalize() throws Throwable {
        deleteTexture();
        super.finalize();
    }
    
    public void updatePaintData(int[] paintData) {
        if (!Arrays.equals(paintData, this.paintData)) {
            this.paintData = paintData.clone();
            needsUpdate = true;
        }
    }
    
    public void updateForResourceLocation(ResourceLocation resourceLocation) {
        if (lastProfileHash == resourceLocation.hashCode() & bufferedPlayerImage != null) {
            return;
        }
        
        BufferedImage bi = null;
        InputStream inputStream = null;
        try {
            ITextureObject skintex = mc.getTextureManager().getTexture(resourceLocation);
            if (skintex instanceof ThreadDownloadImageData) {
                ThreadDownloadImageData imageData = (ThreadDownloadImageData)skintex;
                bi  = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage", "field_110560_d", "bpr.h");
            } else {
                inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
                bi = ImageIO.read(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        if (bi == null) {
            return;
        }
        
        bufferedPlayerImage = SkinHelper.deepCopyBufferedImage(bi);
        lastProfileHash = resourceLocation.hashCode();
        needsUpdate = true;
    }
    
    private void updateTexture() {
        applyPlayerToTexture();
        applyPaintToTexture();
        uploadTexture();
        needsUpdate = false;
    }
    
    private void applyPlayerToTexture() {
        for (int ix = 0; ix < TEXTURE_WIDTH; ix++) {
            for (int iy = 0; iy < TEXTURE_HEIGHT; iy++) {
                if (bufferedPlayerImage == null) {
                    //ModLogger.log("null player image");
                    break;
                }
                bufferedSkinImage.setRGB(ix, iy, bufferedPlayerImage.getRGB(ix, iy));
            }
        }
    }
     
    private void applyPaintToTexture() {
        for (int ix = 0; ix < TEXTURE_WIDTH; ix++) {
            for (int iy = 0; iy < TEXTURE_HEIGHT; iy++) {
                int paintColour = paintData[ix + (iy * TEXTURE_WIDTH)];
                PaintType paintType = PaintType.getPaintTypeFromColour(paintColour);
                if (paintType != PaintType.NONE) {
                    bufferedSkinImage.setRGB(ix, iy, BitwiseUtils.setUByteToInt(paintColour, 0, 255));
                }
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
        if (needsUpdate) {
            updateTexture();
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    }
}
