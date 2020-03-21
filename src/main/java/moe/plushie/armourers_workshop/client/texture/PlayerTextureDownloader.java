package moe.plushie.armourers_workshop.client.texture;

import java.io.File;
import java.util.HashMap;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.handler.PlayerTextureHandler;
import moe.plushie.armourers_workshop.client.render.EntityTextureInfo;
import moe.plushie.armourers_workshop.common.GameProfileCache;
import moe.plushie.armourers_workshop.common.GameProfileCache.IGameProfileCallback;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import moe.plushie.armourers_workshop.proxies.CommonProxy;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    
    public PlayerTexture getPlayerTexture(TextureData textureData) {
        switch (textureData.getTextureType()) {
        case NONE:
            return NO_TEXTURE;
        case USER:
            EntityPlayerSP localPlayer = Minecraft.getMinecraft().player;
            GameProfile localProfile = localPlayer.getGameProfile();
            if (PlayerUtils.gameProfilesMatch(localProfile, textureData.getProfile())) {
                EntityTextureInfo textureInfo = PlayerTextureHandler.INSTANCE.playerTextureMap.get(localProfile);
                PlayerTexture playerTexture = new PlayerTexture(localProfile.getName(), TextureType.USER);
                playerTexture.setModelTypeFromProfile(localProfile);
                playerTexture.setResourceLocation(localPlayer.getLocationSkin());
                if (textureInfo != null && textureInfo.postRender() != null) {
                    playerTexture.setResourceLocation(textureInfo.postRender());
                }
                return playerTexture;
            }
            if (textureData.getProfile() != null) {
                return getPlayerTexture(textureData.getProfile().getName(), TextureType.USER);
            }
            
        case URL:
            return getPlayerTexture(textureData.getUrl(), TextureType.URL);
        }
        return NO_TEXTURE;
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
                        getPlayerTextureFromName(textureString);
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
                Minecraft minecraft = Minecraft.getMinecraft();
                PlayerTexture playerTexture = new PlayerTexture(gameProfile.getName(), TextureType.USER);
                playerTexture.setModelTypeFromProfile(gameProfile);
                minecraft.getSkinManager().loadProfileTextures(gameProfile, new DownloadWrapper(playerTexture), false);
                playerTextureMap.put(gameProfile.getName(), playerTexture);
            }
        }
        return NO_TEXTURE;
    }
    
    private GameProfile getGameProfile(String username) {
        if (!StringUtils.isNullOrEmpty(username)) {
            CommonProxy proxy = ArmourersWorkshop.getProxy();
            if (proxy.isLocalPlayer(username)) {
                if (proxy.haveFullLocalProfile()) {
                    return proxy.getLocalGameProfile();
                }
                return null;
            }
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
        minecraft.getSkinManager().loadProfileTextures(gameProfile, new DownloadWrapper(playerTexture), false);
        synchronized (playerTextureMap) {
            playerTextureMap.put(gameProfile.getName(), playerTexture);
        }
    }
    
    private ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocation, String textureString, PlayerTexture playerTexture, TextureType textureType) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        Object object = texturemanager.getTexture(resourceLocation);
        if (object == null) {
            object = new ModThreadDownloadImageData((File)null, textureString, DefaultPlayerSkin.getDefaultSkinLegacy(), new ImageBufferDownload(), playerTexture);
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
        public void skinAvailable(Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture) {
            if (typeIn == Type.SKIN) {
                playerTexture.setResourceLocation(location);
            }
        }
    }
}
