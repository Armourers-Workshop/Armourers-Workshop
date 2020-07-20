package moe.plushie.armourers_workshop.client.skin.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.model.bake.ModelBakery;
import moe.plushie.armourers_workshop.client.model.bake.ModelBakery.BakedSkin;
import moe.plushie.armourers_workshop.client.model.bake.ModelBakery.IBakedSkinReceiver;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientRequestSkinData;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientSkinCache implements RemovalListener<ISkinIdentifier, Skin>, IBakedSkinReceiver {

    public static ClientSkinCache INSTANCE;

    public static Skin errorSkin = null;

    /** Cache of skins that are in memory. */
    private final Cache<ISkinIdentifier, Skin> skinCache;

    /** List of skins that need to be cleaned up. */
    private final ArrayList<Skin> cleanupList;

    /** Skin IDs that have been requested from the server. */
    private final Cache<ISkinIdentifier, Boolean> requestedSkins;

    private final Executor skinRequestExecutor;

    public static void init() {
        INSTANCE = new ClientSkinCache();
    }

    protected ClientSkinCache() {
        CacheBuilder builder = null;
        builder = CacheBuilder.newBuilder();
        builder.removalListener(this);
        builder.recordStats();
        if (ConfigHandlerClient.skinCacheExpireTime > 0) {
            builder.expireAfterAccess(ConfigHandlerClient.skinCacheExpireTime, TimeUnit.SECONDS);
        }
        if (ConfigHandlerClient.skinCacheMaxSize > 0) {
            builder.maximumSize(ConfigHandlerClient.skinCacheMaxSize);
        }
        skinCache = builder.build();
        cleanupList = new ArrayList<Skin>();
        builder = CacheBuilder.newBuilder();
        builder.expireAfterWrite(20, TimeUnit.SECONDS);
        requestedSkins = builder.build();
        skinRequestExecutor = Executors.newFixedThreadPool(1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public Skin getSkin(ISkinDescriptor descriptor) {
        return getSkin(descriptor.getIdentifier(), true);
    }

    public Skin getSkin(ISkinDescriptor descriptor, boolean requestSkin) {
        return getSkin(descriptor.getIdentifier(), requestSkin);
    }

    public Skin getSkin(ISkinIdentifier identifier) {
        return getSkin(identifier, true);
    }

    public Skin getSkin(ISkinIdentifier identifier, boolean requestSkin) {
        Skin skin = skinCache.getIfPresent(identifier);
        if (skinCache.asMap().containsKey(identifier)) {
            return skinCache.getIfPresent(identifier);
        }
        if (requestSkin) {
            requestSkinFromServer(identifier);
        }
        return null;
    }

    public void requestSkinFromServer(ISkinDescriptor skinPointer) {
        requestSkinFromServer(skinPointer.getIdentifier());
    }

    private void requestSkinFromServer(ISkinIdentifier identifier) {
        if (!identifier.isValid()) {
            return;
        }
        synchronized (requestedSkins) {
            if (!requestedSkins.asMap().containsKey(identifier) & requestedSkins.asMap().size() < ConfigHandlerClient.maxSkinRequests) {
                skinRequestExecutor.execute(new SkinRequestThread(identifier));
                requestedSkins.put(identifier, true);
            }
        }
    }

    public boolean isSkinInCache(ISkinDescriptor skinPointer) {
        return isSkinInCache(skinPointer.getIdentifier());
    }

    public boolean isSkinInCache(ISkinIdentifier identifier) {
        return skinCache.asMap().containsKey(identifier);
    }

    public void markSkinAsDirty(ISkinIdentifier identifier) {
        skinCache.invalidate(identifier);
    }

    @Override
    public void onBakedSkin(BakedSkin bakedSkin) {
        if (bakedSkin.getSkin() == null) {
            bakedSkin = new BakedSkin(errorSkin, bakedSkin.getSkinIdentifierRequested(), bakedSkin.getSkinIdentifierUpdated(), null);
        }
        SkinIdentifier identifierRequested = bakedSkin.getSkinIdentifierRequested();
        synchronized (requestedSkins) {
            if (skinCache.asMap().containsKey(identifierRequested)) {
                // We already have this skin, remove the old one before adding the new one.
                Skin oldSkin = skinCache.getIfPresent(identifierRequested);
                skinCache.invalidate(identifierRequested);
                oldSkin.cleanUpDisplayLists();
                ModLogger.log("removing skin");
            }
            if (requestedSkins.asMap().containsKey(identifierRequested)) {
                skinCache.put(identifierRequested, bakedSkin.getSkin());
                requestedSkins.invalidate(identifierRequested);
            } else {
                // We did not request this skin.
                skinCache.put(bakedSkin.getSkinIdentifierUpdated(), bakedSkin.getSkin());
                ModLogger.log(Level.WARN, "Got an unknown skin - Identifier: " + bakedSkin.getSkinIdentifierUpdated().toString());
            }
        }
    }

    public int getCacheSize() {
        return skinCache.asMap().size();
    }

    public int getRequestQueueSize() {
        synchronized (requestedSkins) {
            return requestedSkins.asMap().size();
        }
    }

    public int getModelCount() {
        int count = 0;
        // Used collection view so access time is not reset.
        Collection<Skin> skins = skinCache.asMap().values();
        Iterator<Skin> iterator = skins.iterator();
        while (iterator.hasNext()) {
            Skin skin = iterator.next();
            if (skin != null) {
                count += skin.getModelCount();
            }
        }
        return count;
    }

    public int getPartCount() {
        int count = 0;
        // Used collection view so access time is not reset.
        Collection<Skin> skins = skinCache.asMap().values();
        Iterator<Skin> iterator = skins.iterator();
        while (iterator.hasNext()) {
            Skin skin = iterator.next();
            if (skin != null) {
                count += skin.getPartCount();
            }
        }
        return count;
    }

    public void clearCache() {
        skinCache.invalidateAll();
        synchronized (requestedSkins) {
            requestedSkins.asMap().size();
        }
    }

    public CacheStats getStats() {
        return skinCache.stats();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT & event.phase == Phase.END) {
            cleanupCheck();
        }
    }

    private void cleanupCheck() {
        synchronized (cleanupList) {
            for (int i = cleanupList.size() - 1; i >= 0; i--) {
                cleanupList.get(i).cleanUpDisplayLists();
                cleanupList.remove(i);
            }
        }
    }

    @Override
    public void onRemoval(RemovalNotification<ISkinIdentifier, Skin> notification) {
        synchronized (cleanupList) {
            cleanupList.add(notification.getValue());
        }
    }

    private static class SkinRequestThread implements Runnable {

        private ISkinIdentifier skinIdentifier;

        public SkinRequestThread(ISkinIdentifier skinIdentifier) {
            this.skinIdentifier = skinIdentifier;
        }

        @Override
        public void run() {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            Skin skin = FastCache.INSTANCE.loadSkin(skinIdentifier);
            if (skin != null) {
                ModelBakery.INSTANCE.receivedUnbakedModel(skin, new SkinIdentifier(skinIdentifier), new SkinIdentifier(skinIdentifier), ClientSkinCache.INSTANCE);
            } else {
                PacketHandler.networkWrapper.sendToServer(new MessageClientRequestSkinData(skinIdentifier));
            }
        }
    }
}
