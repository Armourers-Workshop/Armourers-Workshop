 package riskyken.armourersWorkshop.common.skin.cache;

import java.util.HashSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinIdentifier;
import riskyken.armourersWorkshop.common.library.global.SkinDownloader.DownloadSkinCallable;
import riskyken.armourersWorkshop.common.skin.data.Skin;

public class SkinCacheGlobal {
    
    public static final SkinCacheGlobal INSTANCE = new SkinCacheGlobal();
    
    private final HashSet<Integer> downloadingSet;
    private final Executor executorSkinDownloader;
    private final CompletionService<Skin> completionServiceSkinDownloader;
    
    public SkinCacheGlobal() {
        downloadingSet = new HashSet<Integer>();
        executorSkinDownloader= Executors.newFixedThreadPool(2);
        completionServiceSkinDownloader = new ExecutorCompletionService<Skin>(executorSkinDownloader);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    public void downloadSkin(ISkinIdentifier identifier) {
        synchronized (downloadingSet) {
            if (!downloadingSet.contains(identifier.getSkinGlobalId())) {
                downloadingSet.add(identifier.getSkinGlobalId());
                completionServiceSkinDownloader.submit(new DownloadSkinCallable(null, identifier.getSkinGlobalId()));
            }
        }
    }
    
    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.side == Side.SERVER && event.type == Type.SERVER && event.phase == Phase.END) {
            update();
        }
    }
    
    public void update() {
        Future<Skin> futureSkin = completionServiceSkinDownloader.poll();
        if (futureSkin!= null) {
            try {
                Skin skin = futureSkin.get();
                if (skin != null) {
                    synchronized (downloadingSet) {
                        CommonSkinCache.INSTANCE.onGlobalSkinDownload(skin, skin.serverId);
                        downloadingSet.remove(skin.serverId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
