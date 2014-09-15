package riskyken.armourersWorkshop.client.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiUpdateNakedInfo;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.relauncher.ReflectionHelper;


public class PlayerSkinInfo {
    
    private BufferedImage playerBackupSkin = null;
    private BufferedImage playerNakedSkin = null;
    
    private boolean isNaked;
    private int skinColour;
    private int pantsColour;
    
    private boolean haveSkinBackup;
    private boolean hasNakedSkin;
    private boolean isNakedSkinUploaded;
    
    public PlayerSkinInfo(boolean naked, int skinColour, int pantsColour) {
        this.isNaked = naked;
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
    }
    
    public void setNakedInfo(boolean naked, int skinColour, int pantsColour) {
        if (this.skinColour != skinColour | this.pantsColour != pantsColour) {
            this.hasNakedSkin = false;
            this.isNakedSkinUploaded = false;
        }
        
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
        
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
            for (int iy = 0; iy < 2; iy++) {
                Color c = new Color(playerBackupSkin.getRGB(ix + 11, iy + 13));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 4;
        g = g / 4;
        b = b / 4;
        
        ModLogger.log("R:" + r + " G:" + g + " B:" + b);
        
        int newColour = new Color(r, g, b).getRGB();
        
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiUpdateNakedInfo(this.isNaked, newColour, this.pantsColour));
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
        BufferedImage bufferedImage = getBufferedImageSkin(player);
        if (bufferedImage != null) {
            playerBackupSkin = bufferedImage;
            haveSkinBackup = true;
        }
    }
    
    private void restorePlayerSkin(AbstractClientPlayer player) {
        uploadTexture(player.getLocationSkin(), playerBackupSkin);
        isNakedSkinUploaded = false;
    }
    
    private void uploadNakedSkin(AbstractClientPlayer player) {
        
        ResourceLocation skin = AbstractClientPlayer.locationStevePng;
        if (player.func_152123_o()) {
            skin = player.getLocationSkin();
        }
        uploadTexture(skin, playerNakedSkin);
        isNakedSkinUploaded = true;
    }
    
    private void makeNakedSkin(AbstractClientPlayer player) {
        if (!haveSkinBackup) {
            makeBackupSkin(player);
        }
        
        if (playerBackupSkin == null) { return; }
        
        playerNakedSkin = deepCopy(playerBackupSkin);
        
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
    
    private void uploadTexture(ResourceLocation resourceLocation, BufferedImage bufferedImage) {
        ITextureObject textureObject = Minecraft.getMinecraft().getTextureManager().getTexture(resourceLocation);
        if (textureObject != null) {
            uploadTexture(textureObject, bufferedImage);
        }
    }
    
    private void uploadTexture(ITextureObject textureObject, BufferedImage bufferedImage) {
        TextureUtil.uploadTextureImage(textureObject.getGlTextureId(), bufferedImage);
    }
    
    private BufferedImage getBufferedImageSkin(AbstractClientPlayer player) {
        BufferedImage bufferedImage = null;
        ResourceLocation skin = AbstractClientPlayer.locationStevePng;
        
        if (player.func_152123_o()) {
            ThreadDownloadImageData imageData = AbstractClientPlayer.getDownloadImageSkin(player.getLocationSkin(), player.getCommandSenderName());
            bufferedImage = ReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, imageData, "bufferedImage");
        } else {
            InputStream inputStream = null;
            try {
                inputStream = Minecraft.getMinecraft().getResourceManager().getResource(skin).getInputStream();
                bufferedImage = ImageIO.read(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
        
        return bufferedImage;
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
    
    static BufferedImage deepCopy(BufferedImage bufferedImage) {
        ColorModel cm = bufferedImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bufferedImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
       }
}
