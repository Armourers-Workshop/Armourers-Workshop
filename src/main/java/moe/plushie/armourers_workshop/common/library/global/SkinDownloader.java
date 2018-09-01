package moe.plushie.armourers_workshop.common.library.global;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

import org.apache.commons.io.IOUtils;

import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import net.minecraft.util.StringUtils;

public final class SkinDownloader {
    
    public static void downloadSkin(CompletionService<Skin> skinCompletion, int serverId) {
        skinCompletion.submit(new DownloadSkinCallable(null, serverId));
    }
    
    /**
     * Downloads a fresh skins from the server.
     * @param name
     * @param serverId
     * @return
     */
    public static Skin downloadSkin(String fileName, int serverId) {
        Skin skin = null;
        
        long startTime = System.currentTimeMillis();
        long maxRate = 10;
        
        InputStream in = null;
        String data = null;
        ByteArrayInputStream byteIn = null;
        try {
            in = new URL(String.format("http://plushie.moe/armourers_workshop/download-skin.php?skinid=%d&skinFileName=%s", serverId, fileName)).openStream();
            byte[] skinData = IOUtils.toByteArray(in);
            byteIn = new ByteArrayInputStream(skinData);
            skin = SkinIOUtils.loadSkinFromStream(byteIn);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
        }
        
        long waitTime = maxRate - (System.currentTimeMillis() - startTime);
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (skin != null) {
            skin.serverId = serverId;
        } else {
            ModLogger.log(String.format("Failed to download skin: %s", fileName));
        }
        return skin;
    }
    
    public static class DownloadSkinCallable implements Callable<Skin> {
        
        private final String name;
        private final int serverId;
        
        public DownloadSkinCallable(String name, int serverId) {
            this.name = name;
            this.serverId = serverId;
        }
        
        @Override
        public Skin call() throws Exception {
            Skin skin = downloadSkin(name, serverId);
            return skin;
        }
        
        private Skin downloadSkin(String name, int serverId) throws InterruptedException {
            //Check if we already have the skin in the cache.
            /*
            SkinIdentifier identifier = new SkinIdentifier(0, null, serverId);
            Skin skin = ClientSkinCache.INSTANCE.getSkin(identifier, false);
            if (skin != null) {
                skin.serverId = serverId;
                return skin;
            }
            */
            
            long startTime = System.currentTimeMillis();
            long maxRate = 5;
            
            //ModLogger.log("Downloading skin id: " + serverId);
            
            String downloadUrl = "http://plushie.moe/armourers_workshop/";
            if (!StringUtils.isNullOrEmpty(name)) {
                downloadUrl += "skins/" + name;
            } else {
                downloadUrl += "get-skin.php?skinid=" + String.valueOf(serverId);
            }
            
            Skin skin = null;
            InputStream in = null;
            ByteArrayInputStream byteIn = null;
            String data = null;
            try {
                in = new URL(downloadUrl).openStream();
                byte[] skinData = IOUtils.toByteArray(in);
                byteIn = new ByteArrayInputStream(skinData);
                skin = SkinIOUtils.loadSkinFromStream(byteIn);
                //skin = SkinIOUtils.loadSkinFromStream(new BufferedInputStream(in));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(byteIn);
                IOUtils.closeQuietly(in);
            }
            
            long waitTime = maxRate - (System.currentTimeMillis() - startTime);
            if (waitTime > 0) {
                //Thread.sleep(waitTime);
            }
            
            if (skin != null) {
                skin.serverId = serverId;
            } else {
                ModLogger.log(String.format("Failed to download skin: %d", serverId));
            }
            return skin;
        }
    }
}
