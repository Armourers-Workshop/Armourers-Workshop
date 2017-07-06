package riskyken.armourersWorkshop.client.texture;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin.TextureType;

public class PlayerTexture {
    
    private ResourceLocation resourceLocation;
    private boolean slimModel;
    private boolean downloaded;
    private TextureType textureType;
    private String textureString;
    private long downloadTime;
    
    public PlayerTexture(String textureString, TextureType textureType) {
        this.textureString = textureString;
        this.textureType = textureType;
        resourceLocation = AbstractClientPlayer.locationStevePng;
    }
    
    public void textureDownloaded(boolean slimModel) {
        this.slimModel = slimModel;
        if (!StringUtils.isNullOrEmpty(textureString)) {
            if (textureType == TextureType.URL) {
                resourceLocation = new ResourceLocation("skins/" + StringUtils.stripControlCodes(textureString));
            } else {
                resourceLocation = new ResourceLocation(LibModInfo.ID.toLowerCase(), StringUtils.stripControlCodes(textureString));
            }
        }
        downloadTime = System.currentTimeMillis();
        downloaded = true;
    }
    
    public TextureType getTextureType() {
        return textureType;
    }
    
    public boolean isDownloaded() {
        return downloaded;
    }
    
    public long getDownloadTime() {
        return downloadTime;
    }

    public boolean isSlimModel() {
        return slimModel;
    }
    
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}
