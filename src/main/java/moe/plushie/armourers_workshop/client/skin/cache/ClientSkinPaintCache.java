package moe.plushie.armourers_workshop.client.skin.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.client.skin.SkinTextureKey;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSkinPaintCache implements RemovalListener<SkinTextureKey, SkinModelTexture>, Runnable {

    public static ClientSkinPaintCache INSTANCE = new ClientSkinPaintCache();

    private final Cache<SkinTextureKey, SkinModelTexture> textureCache;
    private final HashSet<TextureGenInfo> requestSet;
    private final ArrayList<TextureGenInfo> requestList;
    private volatile Thread textureGenThread;

    public ClientSkinPaintCache() {
        CacheBuilder builder = CacheBuilder.newBuilder();
        builder.removalListener(this);
        if (ConfigHandlerClient.textureCacheExpireTime > 0) {
            builder.expireAfterAccess(ConfigHandlerClient.textureCacheExpireTime, TimeUnit.SECONDS);
        }
        if (ConfigHandlerClient.textureCacheMaxSize > 0) {
            builder.maximumSize(ConfigHandlerClient.textureCacheMaxSize);
        }
        textureCache = builder.build();
        requestSet = new HashSet<TextureGenInfo>();
        requestList = new ArrayList<TextureGenInfo>();
        textureGenThread = new Thread(this, "Texture Gen Thread");
        textureGenThread.setPriority(Thread.MIN_PRIORITY);
        textureGenThread.start();
        FMLCommonHandler.instance().bus().register(this);
    }

    public SkinModelTexture getTextureForSkin(Skin skin, ISkinDye skinDye, IExtraColours extraColours) {
        if (extraColours == null) {
            extraColours = ExtraColours.EMPTY_COLOUR;
        }
        SkinTextureKey cmk = new SkinTextureKey(skin.lightHash(), skinDye, extraColours);
        return getTextureForSkin(skin, cmk);
    }

    public SkinModelTexture getTextureForSkin(Skin skin, SkinTextureKey cmk) {
        SkinModelTexture st = textureCache.getIfPresent(cmk);
        if (st != null) {
            return st;
        } else {
            TextureGenInfo tgi = new TextureGenInfo(skin, cmk);
            synchronized (requestSet) {
                if (!requestSet.contains(tgi)) {
                    requestSet.add(tgi);
                    synchronized (requestList) {
                        requestList.add(tgi);
                    }
                }
            }
            return skin.skinModelTexture;
        }
    }

    public int size() {
        textureCache.cleanUp();
        return (int) textureCache.size();
    }

    public void clear() {
        textureCache.invalidateAll();
    }

    @Override
    public void onRemoval(RemovalNotification<SkinTextureKey, SkinModelTexture> notification) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                cleanupTexture(notification.getValue());
            }
        });
    }

    private void cleanupTexture(SkinModelTexture modelTexture) {
        if (modelTexture != null) {
            modelTexture.deleteGlTexture();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        textureGenThread = null;
        super.finalize();
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        while (textureGenThread == thisThread) {
            try {
                thisThread.sleep(100);
                genTextures();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void genTextures() {
        SkinModelTexture smt = null;
        TextureGenInfo tgi = null;
        synchronized (requestList) {
            if (requestList.size() > 0) {
                tgi = requestList.get(requestList.size() - 1);
                requestList.remove(requestList.size() - 1);
                smt = new SkinModelTexture();
                smt.createTextureForColours(tgi.skin, tgi.cmk);
            }
        }
        if (smt != null && tgi != null) {
            textureCache.put(tgi.cmk, smt);
            synchronized (requestSet) {
                requestSet.remove(tgi);
            }
        }
    }

    protected class TextureGenInfo {
        public Skin skin;
        public SkinTextureKey cmk;

        public TextureGenInfo(Skin skin, SkinTextureKey cmk) {
            this.skin = skin;
            this.cmk = cmk;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((cmk == null) ? 0 : cmk.hashCode());
            result = prime * result + ((skin == null) ? 0 : skin.hashCode());
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
            TextureGenInfo other = (TextureGenInfo) obj;
            if (cmk == null) {
                if (other.cmk != null)
                    return false;
            } else if (!cmk.equals(other.cmk))
                return false;
            if (skin == null) {
                if (other.skin != null)
                    return false;
            } else if (!skin.equals(other.skin))
                return false;
            return true;
        }
    }
}
