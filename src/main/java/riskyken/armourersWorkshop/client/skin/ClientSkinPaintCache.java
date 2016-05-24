package riskyken.armourersWorkshop.client.skin;

import java.util.ArrayList;
import java.util.HashSet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap;
import riskyken.armourersWorkshop.common.data.ExpiringHashMap.IExpiringMapCallback;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinTexture;

@SideOnly(Side.CLIENT)
public class ClientSkinPaintCache implements IExpiringMapCallback, Runnable {
    
    public static ClientSkinPaintCache INSTANCE = new ClientSkinPaintCache();
    
    private final ExpiringHashMap<SkinTextureKey, SkinModelTexture> textureMap;
    private final HashSet<TextureGenInfo> requestSet;
    private final ArrayList<TextureGenInfo> requestList;
    private volatile Thread textureGenThread;
    
    public ClientSkinPaintCache() {
        textureMap = new ExpiringHashMap<SkinTextureKey, SkinModelTexture>(1000 * ConfigHandler.clientTextureCacheTime, this);
        requestSet = new HashSet<TextureGenInfo>();
        requestList = new ArrayList<TextureGenInfo>();
        textureGenThread = new Thread(this, "Texture Gen Thread");
        textureGenThread.start();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public SkinModelTexture getTextureForSkin(Skin skin, ISkinDye skinDye, byte[] extraColours) {
        if (extraColours == null) {
            extraColours = new byte[] {127, 127, 127, 127, 127, 127};
        }
        SkinTextureKey cmk = new SkinTextureKey(skin.lightHash(), skinDye, extraColours);
        return getTextureForSkin(skin, cmk);
    }
    
    public SkinModelTexture getTextureForSkin(Skin skin, SkinTextureKey cmk) {
        SkinModelTexture st = textureMap.get(cmk);
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
        return textureMap.size();
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.END) {
            textureMap.cleanupCheck();
        }
    }

    @Override
    public void itemExpired(Object mapItem) {
        if (mapItem != null && mapItem instanceof SkinTexture) {
            ((SkinModelTexture)mapItem).deleteGlTexture();
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
            synchronized (textureMap) {
                textureMap.put(tgi.cmk, smt);
            }
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
