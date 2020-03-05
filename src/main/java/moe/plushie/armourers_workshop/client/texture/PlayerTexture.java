package moe.plushie.armourers_workshop.client.texture;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;

import com.google.common.collect.Iterables;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class PlayerTexture {
    
    public static final PlayerTexture NO_TEXTURE  = new PlayerTexture("", TextureType.NONE);
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
        resourceLocation = getSteveResourceLocation();
    }
    
    private ResourceLocation getSteveResourceLocation() {
        return new ResourceLocation("textures/entity/steve.png");
    }
    
    public void textureDownloaded(boolean slimModel) {
        this.slimModel = slimModel;
        if (!StringUtils.isNullOrEmpty(textureString)) {
            if (textureType == TextureType.URL) {
                resourceLocation = new ResourceLocation("skins/" + StringUtils.stripControlCodes(textureString));
            } else {
                resourceLocation = new ResourceLocation(LibModInfo.ID, StringUtils.stripControlCodes(textureString));
            }
        }
        downloadTime = System.currentTimeMillis();
        downloaded = true;
    }
    
    public void setModelTypeFromProfile(GameProfile gameProfile) {
        if (gameProfile != null) {
            if (gameProfile.getProperties().containsKey("textures")) {
                Property property = (Property)Iterables.getFirst(gameProfile.getProperties().get("textures"), (Object)null);
                try {
                    String json = new String(Base64.decodeBase64(property.getValue()), Charsets.UTF_8);
                    //ModLogger.log("Full json: " + json);
                    JsonParser parser = new JsonParser();
                    JsonElement jsonElement = parser.parse(json);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    
                    JsonObject jsonTextures = jsonObject.getAsJsonObject("textures");
                    JsonObject jsonSkin = jsonTextures.getAsJsonObject("SKIN");
                    if (jsonSkin != null && jsonSkin.has("metadata")) {
                        slimModel = true;
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
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
    
    public void setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        downloadTime = System.currentTimeMillis();
        downloaded = true;
    }
}
