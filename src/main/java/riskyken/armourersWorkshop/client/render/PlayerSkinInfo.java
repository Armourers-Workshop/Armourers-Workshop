package riskyken.armourersWorkshop.client.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo.PlayerSkinTextureType;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class PlayerSkinInfo {
    
    private ResourceLocation nakedSkinLocation = null;
    
    private EntityNakedInfo nakedInfo;
    
    /** Has a skin backup been made? **/
    private boolean haveSkinBackup;
    
    private int backupId;
    
    /** Has a naked skin been made? **/
    private boolean hasNakedSkin;
    
    /** The last skin the player had. **/
    private ResourceLocation lastSkin;
    
    public PlayerSkinInfo(EntityNakedInfo nakedInfo) {
        this.nakedInfo = nakedInfo;
    }
    
    public void setSkinInfo(EntityNakedInfo nakedInfo) {
        if (this.nakedInfo.skinColour != nakedInfo.skinColour |
                this.nakedInfo.hairColour != nakedInfo.hairColour |
                this.nakedInfo.pantsColour != nakedInfo.pantsColour | 
                this.nakedInfo.pantStripeColour != nakedInfo.pantStripeColour |
                this.nakedInfo.skinTextureType != nakedInfo.skinTextureType) {
            this.hasNakedSkin = false;
        }
        
        this.nakedInfo = nakedInfo;
    }
    
    public EntityNakedInfo getNakedInfo() {
        return nakedInfo;
    }

    public int autoColourHair(AbstractClientPlayer player) {
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(player);
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerSkin.getRGB(ix + 11, iy + 3));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        return new Color(r, g, b).getRGB();
    }
    
    public int autoColourSkin(AbstractClientPlayer player) {
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(player);
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerSkin.getRGB(ix + 11, iy + 13));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        return new Color(r, g, b).getRGB();
    }
    
    public void preRender(AbstractClientPlayer player, RenderPlayer renderer) {
        if (player.func_152123_o()) {
            checkSkin(player);
            if (nakedInfo.skinTextureType != PlayerSkinTextureType.DEFAULT) {
                lastSkin = player.getLocationSkin();
                player.func_152121_a(MinecraftProfileTexture.Type.SKIN , nakedSkinLocation);
            }
        }
    	renderer.modelBipedMain.bipedHeadwear.isHidden = this.nakedInfo.headOverlay;
    }
    
    public void postRender(AbstractClientPlayer player, RenderPlayer renderer) {
        if (player.func_152123_o()) {
            if (nakedInfo.skinTextureType != PlayerSkinTextureType.DEFAULT) {
                player.func_152121_a(MinecraftProfileTexture.Type.SKIN , lastSkin);
                lastSkin = null;
            }
        }
    	renderer.modelBipedMain.bipedHeadwear.isHidden = false;
    }
    
    public void checkSkin(AbstractClientPlayer player) {
        if (nakedInfo.skinTextureType != PlayerSkinTextureType.DEFAULT) {
            if (!hasNakedSkin) {
                makeNakedSkin(player);
            }
        }
        //TODO remove naked skin when not in use.
    }
    
    private void makeNakedSkin(AbstractClientPlayer player) {
        BufferedImage playerSkin = SkinHelper.getBufferedImageSkin(player);
        BufferedImage playerNakedSkin = SkinHelper.deepCopyBufferedImage(playerSkin);
        
        
        if (nakedInfo.skinTextureType == PlayerSkinTextureType.NONE) {
            for (int ix = 0; ix < 56; ix++) {
                for (int iy = 0; iy < 16; iy++) {
                    playerNakedSkin.setRGB(ix, iy + 16, nakedInfo.skinColour);
                }
            }
        }
        if (nakedInfo.skinTextureType == PlayerSkinTextureType.LEGS) {
            for (int ix = 0; ix < 16; ix++) {
                for (int iy = 0; iy < 16; iy++) {
                    playerNakedSkin.setRGB(ix, iy + 16, nakedInfo.skinColour);
                }
            }
        }
        
        //Pants!
        for (int ix = 0; ix < 16; ix++) {
            playerNakedSkin.setRGB(ix, 20, nakedInfo.pantsColour);
        }
        for (int ix = 0; ix < 8; ix++) {
            playerNakedSkin.setRGB(ix + 6, 20 + 1, nakedInfo.pantStripeColour);
        }
        for (int ix = 0; ix < 6; ix++) {
            playerNakedSkin.setRGB(ix + 7, 20 + 2, nakedInfo.pantsColour);
        }
        
        Minecraft mc = Minecraft.getMinecraft();
        
        if (nakedSkinLocation != null) {
            mc.renderEngine.deleteTexture(nakedSkinLocation);
        }
        
        SkinTextureObject sto = new SkinTextureObject(playerNakedSkin);
        nakedSkinLocation = new ResourceLocation(LibModInfo.ID.toLowerCase(), String.valueOf(playerNakedSkin.hashCode()));
        mc.renderEngine.loadTexture(nakedSkinLocation, sto);
        hasNakedSkin = true;
    }
    
    private class SkinTextureObject implements ITextureObject {
        
        private final int textureId;
        
        public SkinTextureObject(BufferedImage texture) {
            textureId = TextureUtil.glGenTextures();
            TextureUtil.uploadTextureImage(textureId, texture);
        }
        
        @Override
        public void loadTexture(IResourceManager p_110551_1_) throws IOException {
            //NO-OP
        }

        @Override
        public int getGlTextureId() {
            return this.textureId;
        }
    }
}
