package riskyken.armourersWorkshop.client.render;

import java.awt.Color;
import java.awt.image.BufferedImage;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiUpdateNakedInfo;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import riskyken.armourersWorkshop.utils.ModLogger;


public class PlayerSkinInfo {
    
    private BufferedImage playerBackupSkin = null;
    private BufferedImage playerNakedSkin = null;
    
    private EntityNakedInfo nakedInfo;
    
    /** Has a skin backup been made? **/
    private boolean haveSkinBackup;
    
    private int backupId;
    
    /** Has a naked skin been made? **/
    private boolean hasNakedSkin;
    
    /** Is the naked skin uploaded? **/
    private boolean isNakedSkinUploaded;
    
    public PlayerSkinInfo(EntityNakedInfo nakedInfo) {
        this.nakedInfo = nakedInfo;
    }
    
    public void setSkinInfo(EntityNakedInfo nakedInfo) {
        if (this.nakedInfo.skinColour != nakedInfo.skinColour |
                this.nakedInfo.hairColour != nakedInfo.hairColour |
                this.nakedInfo.pantsColour != nakedInfo.pantsColour | 
                this.nakedInfo.pantStripeColour != nakedInfo.pantStripeColour) {
            this.hasNakedSkin = false;
            this.isNakedSkinUploaded = false;
        }
        
        this.nakedInfo = nakedInfo;
    }
    
    public EntityNakedInfo getNakedInfo() {
        return nakedInfo;
    }

    public void autoColourHair(AbstractClientPlayer player) {
        if (playerBackupSkin == null) {
            return;
        }
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerBackupSkin.getRGB(ix + 11, iy + 3));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        int newColour = new Color(r, g, b).getRGB();
        
        EntityNakedInfo newNakedInfo = new EntityNakedInfo();
        newNakedInfo.isNaked = this.nakedInfo.isNaked;
        newNakedInfo.skinColour = this.nakedInfo.skinColour;
        newNakedInfo.hairColour = newColour;
        newNakedInfo.pantsColour = this.nakedInfo.pantsColour;
        newNakedInfo.pantStripeColour = this.nakedInfo.pantStripeColour;
        newNakedInfo.armourOverride = this.nakedInfo.armourOverride;
        newNakedInfo.headOverlay = this.nakedInfo.headOverlay;
        
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiUpdateNakedInfo(newNakedInfo));
    }
    
    public void autoColourSkin(AbstractClientPlayer player) {
        if (playerBackupSkin == null) {
            return;
        }
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerBackupSkin.getRGB(ix + 11, iy + 13));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        int newColour = new Color(r, g, b).getRGB();
        
        EntityNakedInfo newNakedInfo = new EntityNakedInfo();
        newNakedInfo.isNaked = this.nakedInfo.isNaked;
        newNakedInfo.skinColour = newColour;
        newNakedInfo.hairColour = this.nakedInfo.hairColour;
        newNakedInfo.pantsColour = this.nakedInfo.pantsColour;
        newNakedInfo.pantStripeColour = this.nakedInfo.pantStripeColour;
        newNakedInfo.armourOverride = this.nakedInfo.armourOverride;
        newNakedInfo.headOverlay = this.nakedInfo.headOverlay;
        
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiUpdateNakedInfo(newNakedInfo));
    }
    
    public void preRender(AbstractClientPlayer player, RenderPlayer renderer) {
    	checkSkin(player);
    	renderer.modelBipedMain.bipedHeadwear.isHidden = this.nakedInfo.headOverlay;
    }
    
    public void postRender(AbstractClientPlayer player, RenderPlayer renderer) {
    	renderer.modelBipedMain.bipedHeadwear.isHidden = false;
    }
    
    public void checkSkin(AbstractClientPlayer player) {
        if (nakedInfo.isNaked) {
            if (!isNakedSkinUploaded) {
                if (hasNakedSkin) {
                    uploadNakedSkin(player);
                } else {
                    makeNakedSkin(player);
                    uploadNakedSkin(player);
                }
            }
        } else {
            if (isNakedSkinUploaded) {
                restorePlayerSkin(player);
            }
        }
    }
    
    private void makeBackupSkin(AbstractClientPlayer player) {
        if (!player.func_152123_o()) {
            return;
        }
        BufferedImage bufferedImage = SkinHelper.getBufferedImageSkin(player);
        if (bufferedImage != null) {
            playerBackupSkin = bufferedImage;
            backupId = GL11.glGenTextures();
            TextureUtil.uploadTextureImage(backupId, playerBackupSkin);
            haveSkinBackup = true;
            ModLogger.log("Made skin backup for player: " + player.getDisplayName());
        } else {
            ModLogger.log(Level.WARN, "Fail to make skin backup.");
        }
    }
    
    public boolean bindNomalSkin() {
        if (haveSkinBackup) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, backupId);
            return true;
        }
        return false;
    }
    
    private void restorePlayerSkin(AbstractClientPlayer player) {
        SkinHelper.uploadTexture(player.getLocationSkin(), playerBackupSkin);
        isNakedSkinUploaded = false;
    }
    
    private void uploadNakedSkin(AbstractClientPlayer player) {
        if (!hasNakedSkin) {
            return;
        }
        if (playerNakedSkin == null) {
            ModLogger.log(Level.ERROR, "Naked skin missing. Something is wrong!");
            return;
        }
        
        ResourceLocation skin = AbstractClientPlayer.locationStevePng;
        if (player.func_152123_o()) {
            skin = player.getLocationSkin();
        }
        SkinHelper.uploadTexture(skin, playerNakedSkin);
        isNakedSkinUploaded = true;
    }
    
    private void makeNakedSkin(AbstractClientPlayer player) {
        if (!haveSkinBackup) {
            makeBackupSkin(player);
            return;
        }
        
        if (playerBackupSkin == null) {
            return;
        }
        
        playerNakedSkin = SkinHelper.deepCopyBufferedImage(playerBackupSkin);
        
        for (int ix = 0; ix < 56; ix++) {
            for (int iy = 0; iy < 16; iy++) {
                playerNakedSkin.setRGB(ix, iy + 16, nakedInfo.skinColour);
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
        
        uploadNakedSkin(player);
        hasNakedSkin = true;
    }
}
