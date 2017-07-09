package riskyken.armourersWorkshop.client.texture;

import java.io.File;
import java.util.HashMap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.common.data.TextureType;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class PlayerTextureDownloader {
    
    private final HashMap<String, PlayerTexture> playerTextureMap;
    private final String SKIN_DOWNLOAD_URL = "http://skins.minecraft.net/MinecraftSkins/%s.png";
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
                if (lastSkinDownload + 200L < System.currentTimeMillis()) {
                    lastSkinDownload = System.currentTimeMillis();
                    playerTexture = new PlayerTexture(textureString, textureType);
                    playerTextureMap.put(textureString, playerTexture);
                    downloadTexture(textureString, playerTexture, textureType);
                }
            }
        }
        
        return playerTexture;
    }
    
    private void downloadTexture(String textureString, PlayerTexture playerTexture, TextureType textureType) {
        ResourceLocation resourceLocation = null;
        if (textureType == TextureType.URL) {
            resourceLocation = new ResourceLocation("skins/" + StringUtils.stripControlCodes(textureString));
        } else {
            resourceLocation = new ResourceLocation(LibModInfo.ID.toLowerCase(), StringUtils.stripControlCodes(textureString));
        }
        ThreadDownloadImageData imageData = getDownloadImageSkin(resourceLocation, textureString, playerTexture, textureType);
    }
    
    private ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocation, String textureString, PlayerTexture playerTexture, TextureType textureType) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        Object object = texturemanager.getTexture(resourceLocation);

        if (object == null) {
            String downloadUrl = "";
            if (textureType == TextureType.URL) {
                downloadUrl = textureString;
            } else {
                downloadUrl = String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] {StringUtils.stripControlCodes(textureString)});
            }
            object = new ModThreadDownloadImageData((File)null, downloadUrl, AbstractClientPlayer.locationStevePng, new ImageBufferDownload(), playerTexture);
            texturemanager.loadTexture(resourceLocation, (ITextureObject)object);
        }

        return (ThreadDownloadImageData)object;
    }
}
