package riskyken.armourersWorkshop.client.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.BitSet;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiUpdateNakedInfo;


public class PlayerSkinInfo {
    
    private BufferedImage playerBackupSkin = null;
    private BufferedImage playerNakedSkin = null;
    
    private boolean isNaked;
    private int skinColour;
    private int pantsColour;
    private BitSet armourOverride;
    private boolean headOverlay;
    
    private boolean haveSkinBackup;
    private boolean hasNakedSkin;
    private boolean isNakedSkinUploaded;
    
    public PlayerSkinInfo(boolean naked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        this.isNaked = naked;
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
        this.armourOverride = armourOverride;
        this.headOverlay = headOverlay;
    }
    
    public void setSkinInfo(boolean naked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        if (this.skinColour != skinColour | this.pantsColour != pantsColour) {
            this.hasNakedSkin = false;
            this.isNakedSkinUploaded = false;
        }
        
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
        this.armourOverride = armourOverride;
        this.headOverlay = headOverlay;
        
        if (isNaked != naked) {
            this.isNaked = naked;
        }
    }
    

    public void autoColourSkin(AbstractClientPlayer player) {
        if (!haveSkinBackup) {
            makeBackupSkin(player);
        }
        if (playerBackupSkin == null) { return; }
        
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
        
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiUpdateNakedInfo(this.isNaked, newColour, this.pantsColour, this.armourOverride, this.headOverlay));
    }
    
    public void preRender(AbstractClientPlayer player, RenderPlayer renderer) {
    	checkSkin(player);
    	renderer.modelBipedMain.bipedHeadwear.isHidden = this.headOverlay;
    }
    
    public void postRender(AbstractClientPlayer player, RenderPlayer renderer) {
    	renderer.modelBipedMain.bipedHeadwear.isHidden = false;
    }
    
    public void checkSkin(AbstractClientPlayer player) {
        if (isNaked) {
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
        BufferedImage bufferedImage = SkinHelper.getBufferedImageSkin(player);
        if (bufferedImage != null) {
            playerBackupSkin = bufferedImage;
            haveSkinBackup = true;
        }
    }
    
    private void restorePlayerSkin(AbstractClientPlayer player) {
        SkinHelper.uploadTexture(player.getLocationSkin(), playerBackupSkin);
        isNakedSkinUploaded = false;
    }
    
    private void uploadNakedSkin(AbstractClientPlayer player) {
        
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
        }
        
        if (playerBackupSkin == null) { return; }
        
        playerNakedSkin = SkinHelper.deepCopyBufferedImage(playerBackupSkin);
        
        for (int ix = 0; ix < 56; ix++) {
            for (int iy = 0; iy < 16; iy++) {
                playerNakedSkin.setRGB(ix, iy + 16, skinColour);
            }
        }
        
        //Pants!
        for (int ix = 0; ix < 16; ix++) {
            playerNakedSkin.setRGB(ix, 20, pantsColour);
        }
        for (int ix = 0; ix < 8; ix++) {
            playerNakedSkin.setRGB(ix + 6, 20 + 1, pantsColour);
        }
        for (int ix = 0; ix < 6; ix++) {
            playerNakedSkin.setRGB(ix + 7, 20 + 2, pantsColour);
        }
        
        uploadNakedSkin(player);
        hasNakedSkin = true;
    }
    
    public boolean isNaked() {
        return isNaked;
    }
    
    public int getSkinColour() {
        return skinColour;
    }
    
    public int getPantsColour() {
        return pantsColour;
    }
    
    public BitSet getArmourOverride() {
		return armourOverride;
	}
    
    public boolean getHeadOverlay() {
		return headOverlay;
	}
}
