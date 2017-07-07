package riskyken.armourersWorkshop.client.texture;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.common.data.TextureType;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public class PlayerTexture {
    
    private static final String TAG_TEXTURE_STRING = "string";
    private static final String TAG_TEXTURE_TYPE = "type";
    
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
    
    public String getTextureString() {
        return textureString;
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
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setString(TAG_TEXTURE_STRING, textureString);
        compound.setByte(TAG_TEXTURE_TYPE, (byte) textureType.ordinal());
    }
    
    public void readFromNBT(NBTTagCompound compound) {
        textureString = compound.getString(TAG_TEXTURE_STRING);
        textureType = TextureType.values()[compound.getByte(TAG_TEXTURE_TYPE)];
    }
    
    public static PlayerTexture fromNBT(NBTTagCompound compound) {
        PlayerTexture playerTexture = new PlayerTexture("", TextureType.USER);
        playerTexture.readFromNBT(compound);
        return playerTexture;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((textureString == null) ? 0 : textureString.hashCode());
        result = prime * result + ((textureType == null) ? 0 : textureType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlayerTexture other = (PlayerTexture) obj;
        if (textureString == null) {
            if (other.textureString != null)
                return false;
        } else if (!textureString.equals(other.textureString))
            return false;
        if (textureType != other.textureType)
            return false;
        return true;
    }
}
