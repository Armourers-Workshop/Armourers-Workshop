package riskyken.armourersWorkshop.client.texture;

import java.io.File;
import java.util.HashMap;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.common.GameProfileCache;
import riskyken.armourersWorkshop.common.GameProfileCache.IGameProfileCallback;
import riskyken.armourersWorkshop.common.data.TextureType;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public class PlayerTextureDownloader implements IGameProfileCallback {
    
    private final HashMap<String, PlayerTexture> playerTextureMap;
    //private final String SKIN_DOWNLOAD_URL = "http://skins.minecraft.net/MinecraftSkins/%s.png";
    //private final String SKIN_DOWNLOAD_URL = "https://s3.amazonaws.com/MinecraftSkins/%s.png";
    private static final PlayerTexture NO_TEXTURE = new PlayerTexture("", TextureType.USER);
    
    private static long lastSkinDownload = 0;

    public PlayerTextureDownloader() {
        playerTextureMap = new HashMap<String, PlayerTexture>();
    }
    
    public PlayerTexture getPlayerTexture(PlayerTexture playerTexture) {
        return getPlayerTexture(playerTexture.getTextureString(), playerTexture.getTextureType());
    }
    
    public PlayerTexture getPlayerTexture(String textureString, TextureType textureType) {
        PlayerTexture playerTexture = NO_TEXTURE;
        
        synchronized (playerTextureMap) {
            if (playerTextureMap.containsKey(textureString)) {
                playerTexture = playerTextureMap.get(textureString);
            } else {
                if (lastSkinDownload + 250L < System.currentTimeMillis()) {
                    if (textureType == TextureType.URL) {
                        lastSkinDownload = System.currentTimeMillis();
                        playerTexture = new PlayerTexture(textureString, textureType);
                        playerTextureMap.put(textureString, playerTexture);
                        downloadTexture(textureString, playerTexture, textureType);
                    } else {
                        //lastSkinDownload = System.currentTimeMillis();
                        playerTexture = getPlayerTextureFromName(textureString);
                        if (playerTexture != NO_TEXTURE) {
                            ModLogger.log("Setting profile " + textureString);
                            //playerTextureMap.put(textureString, playerTexture);
                        }
                    }
                }
            }
        }
        
        return playerTexture;
    }
    
    private void downloadTexture(String textureString, PlayerTexture playerTexture, TextureType textureType) {
        ResourceLocation resourceLocation = null;
        if (StringUtils.isNullOrEmpty(textureString)) {
            return;
        }
        if (textureType == TextureType.URL) {
            resourceLocation = new ResourceLocation("skins/" + StringUtils.stripControlCodes(textureString));
            ThreadDownloadImageData imageData = getDownloadImageSkin(resourceLocation, textureString, playerTexture, textureType);
        } else {
            ModLogger.log("Setting wrong " + textureString);
            //resourceLocation = new ResourceLocation(LibModInfo.ID.toLowerCase(), StringUtils.stripControlCodes(textureString));
        }
    }
    
    private PlayerTexture getPlayerTextureFromName(String username) {
        GameProfile gameProfile = getGameProfile(username);
        if (gameProfile != null) {
            if (!playerTextureMap.containsKey(gameProfile.getName())) {
                ModLogger.log("Asking for texture " + gameProfile);
                Minecraft minecraft = Minecraft.getMinecraft();
                PlayerTexture playerTexture = new PlayerTexture(gameProfile.getName(), TextureType.USER);
                playerTexture.setModelTypeFromProfile(gameProfile);
                minecraft.func_152342_ad().func_152790_a(gameProfile, new DownloadWrapper(playerTexture), false);
                playerTextureMap.put(gameProfile.getName(), playerTexture);
            }
        }
        return NO_TEXTURE;
    }
    
    private GameProfile getGameProfile(String username) {
        if (!StringUtils.isNullOrEmpty(username)) {
            Minecraft minecraft = Minecraft.getMinecraft();
            
            GameProfile filledProfile = GameProfileCache.getGameProfileClient(username, this);
            if (filledProfile != null) {
                return filledProfile;
            }
        }
        return null;
    }
    
    @Override
    public void profileDownloaded(GameProfile gameProfile) {
        Minecraft minecraft = Minecraft.getMinecraft();
        PlayerTexture playerTexture = new PlayerTexture(gameProfile.getName(), TextureType.USER);
        playerTexture.setModelTypeFromProfile(gameProfile);
        minecraft.func_152342_ad().func_152790_a(gameProfile, new DownloadWrapper(playerTexture), false);
        synchronized (playerTextureMap) {
            playerTextureMap.put(gameProfile.getName(), playerTexture);
        }
    }
    
    private GameProfile getLocalGameProfile() {
        return Minecraft.getMinecraft().thePlayer.getGameProfile();
    }
    
    private ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocation, String textureString, PlayerTexture playerTexture, TextureType textureType) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        Object object = texturemanager.getTexture(resourceLocation);
        if (object == null) {
            object = new ModThreadDownloadImageData((File)null, textureString, AbstractClientPlayer.locationStevePng, new ImageBufferDownload(), playerTexture);
            texturemanager.loadTexture(resourceLocation, (ITextureObject)object);
        }
        return (ThreadDownloadImageData)object;
    }
    
    private static class DownloadWrapper implements SkinAvailableCallback {
        
        private final PlayerTexture playerTexture;
        
        public DownloadWrapper(PlayerTexture playerTexture) {
            this.playerTexture = playerTexture;
        }
        
        @Override
        public void func_152121_a(Type type, ResourceLocation resourceLocation) {
            if (type == Type.SKIN) {
                ModLogger.log("Got for texture " + playerTexture.getTextureString());
                playerTexture.setResourceLocation(resourceLocation);
            }
        }
    }
}
