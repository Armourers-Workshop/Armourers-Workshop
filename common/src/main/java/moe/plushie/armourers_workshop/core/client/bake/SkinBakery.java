package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.data.DataTransformer;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.data.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Environment(EnvType.CLIENT)
public final class SkinBakery implements ISkinLibraryListener {

    private static final SkinBakery EMPTY = new SkinBakery();
    private static SkinBakery BAKERY;

    private final AtomicInteger bakingQueue = new AtomicInteger(0);
    private final AtomicIntegerArray bakeTimes = new AtomicIntegerArray(1000);

    private final ArrayList<IBakeListener> listeners = new ArrayList<>();
    private final DataTransformer<String, BakedSkin, Skin> manager = new DataTransformer.Builder<String, BakedSkin, Skin>()
            .thread("AW-SKIN-BK", Thread.MIN_PRIORITY)
            .loadCount(ModConfig.Client.modelBakingThreadCount)
            .transformCount(ModConfig.Client.modelBakingThreadCount)
            .loader(this::safeLoadSkin2)
            .transformer(this::safeBakeSkin2)
            .build();

    public SkinBakery() {
    }

    public static SkinBakery getInstance() {
        if (BAKERY != null) {
            return BAKERY;
        }
        return EMPTY;
    }

    public static void start() {
        if (BAKERY == null) {
            BAKERY = new SkinBakery();
            BAKERY.startListenLibraryChanges();
            ModLog.debug("start bakery");
        }
    }

    public static void stop() {
        if (BAKERY != null) {
            BAKERY.stopListenLibraryChanges();
            BAKERY.manager.shutdown();
            BAKERY = null;
            SkinVertexBufferBuilder.clearAllCache();
            ModLog.debug("stop bakery");
        }
    }

    public void addListener(IBakeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IBakeListener listener) {
        listeners.remove(listener);
    }

    @Nullable
    public BakedSkin getSkin(String identifier) {
        if (identifier.isEmpty()) {
            return null;
        }
        Pair<BakedSkin, Exception> pair = manager.get(identifier);
        if (pair != null) {
            return pair.getKey();
        }
//        Optional<BakedSkin> skin = manager.get(identifier);
//        if (skin != null && skin.isPresent()) {
//            return skin.get();
//        }
        return null;
    }

    @Nullable
    public BakedSkin loadSkin(String identifier, Ticket ticket) {
        if (identifier.isEmpty()) {
            return null;
        }
        Pair<BakedSkin, Exception> pair = manager.getOrLoad(identifier, ticket);
        if (pair != null) {
            return pair.getKey();
        }
        return null;
    }

    @Nullable
    public BakedSkin loadSkin(SkinDescriptor descriptor, Ticket ticket) {
        if (!descriptor.isEmpty()) {
            return loadSkin(descriptor.getIdentifier(), ticket);
        }
        return null;
    }

    public void loadSkin(String identifier, Ticket ticket, IResultHandler<BakedSkin> handler) {
        manager.load(identifier, ticket, handler);
    }


    private void startListenLibraryChanges() {
        SkinLibraryManager.getClient().addListener(this);
    }

    private void stopListenLibraryChanges() {
        SkinLibraryManager.getClient().removeListener(this);
    }

    @Override
    public void libraryDidChanges(ISkinLibrary library, ISkinLibrary.Difference difference) {
        RenderSystem.recordRenderCall(() -> {
            difference.getRemovedChanges().forEach(it -> manager.remove(it.getSkinIdentifier()));
            difference.getUpdatedChanges().forEach(it -> manager.remove(it.getKey().getSkinIdentifier()));
        });
    }

    private void safeLoadSkin2(String identifier, IResultHandler<Skin> complete) {
        SkinLoader.getInstance().loadSkin(identifier, complete);
    }

    private void safeBakeSkin2(String identifier, Skin skin, IResultHandler<BakedSkin> complete) {
        try {
            bakeSkin(identifier, skin, complete);
        } catch (Exception exception) {
            exception.printStackTrace();
            complete.reject(exception);
        }
    }

    private void bakeSkin(String identifier, Skin skin, IResultHandler<BakedSkin> complete) {
        ModLog.debug("'{}' => start baking skin", identifier);
        long startTime = System.currentTimeMillis();
//            skin.lightHash();
//            int extraDyes = SkinPaintTypes.getInstance().getExtraChannels();
//
//            int[][] dyeColour;
//            int[] dyeUseCount;
//
//            dyeColour = new int[3][extraDyes];
//            dyeUseCount = new int[extraDyes];

//            if (ClientProxy.getTexturePaintType() == TexturePaintType.MODEL_REPLACE_AW) {
//                skin.addPaintDataParts();
//            }

        SkinUsedCounter usedCounter = new SkinUsedCounter();
        ArrayList<BakedSkinPart> bakedParts = new ArrayList<>();

        ColorScheme scheme = new ColorScheme();
        ColorDescriptor colorInfo = new ColorDescriptor();

        skin.getParts().forEach(part -> {
            PackedQuad.from(part).forEach((partType, quads) -> {
                // when has a different part type, it means the skin part was split.
                // for ensure data safety, we need create a blank skin part to manage data.
                SkinPart usedPart = part;
                if (usedPart.getType() != partType) {
                    usedPart = new SkinPart.Empty(partType, quads.getBounds(), quads.getRenderShape());
                }
                BakedSkinPart bakedPart = new BakedSkinPart(usedPart, quads);
                bakedParts.add(bakedPart);
                usedCounter.addFaceTotal(bakedPart.getFaceTotal());
            });
            usedCounter.add(part.getCubeData().getUsedCounter());
            // part.clearCubeData();
        });

        PackedQuad.from(skin.getPaintData()).forEach((partType, quads) -> {
            SkinPart part = new SkinPart.Empty(partType, quads.getBounds(), quads.getRenderShape());
            BakedSkinPart bakedPart = new BakedSkinPart(part, quads);
            bakedParts.add(bakedPart);
        });

        int partId = 0;
        ArrayList<BakedSkinPart> iterator = new ArrayList<>(bakedParts);
        while (!iterator.isEmpty()) {
            BakedSkinPart bakedPart = iterator.remove(0);
            bakedPart.setId(partId++);
            colorInfo.add(bakedPart.getColorInfo());
            iterator.addAll(0, bakedPart.getChildren());
        }

        usedCounter.addPaints(colorInfo.getPaintTypes());

        long totalTime = System.currentTimeMillis() - startTime;
//            int index = bakeTimesIndex.getAndIncrement();
//            if (index > bakeTimes.length() - 1) {
//                index = 0;
//                bakeTimesIndex.set(0);
//            }
//            bakeTimes.set(index, (int) totalTime);

        BakedSkin bakedSkin = new BakedSkin(identifier, skin, scheme, usedCounter, colorInfo, bakedParts);
        ModLog.debug("'{}' => accept baked skin, time: {}ms", identifier, totalTime);
        complete.accept(bakedSkin);
        RenderSystem.recordRenderCall(() -> notifyBake(identifier, bakedSkin));

        // if bake speed too fast, cause system I/O too high.
        if (totalTime < 250) {
            sleep(100);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
        }
    }

    private void notifyBake(String identifier, BakedSkin bakedSkin) {
        listeners.forEach(listener -> listener.didBake(identifier, bakedSkin));
    }

    public int getAverageBakeTime() {
        int totalItems = 0;
        int totalTime = 0;
        for (int i = 0; i < bakeTimes.length(); i++) {
            int time = bakeTimes.get(i);
            if (time != 0) {
                totalItems++;
                totalTime += time;
            }
        }
        return (int) ((double) totalTime / (double) totalItems);
    }

    public int getBakingQueueSize() {
        return bakingQueue.get();
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface IBakeListener {
        void didBake(String identifier, BakedSkin bakedSkin);
    }

//    public void handleModelDownload(Thread downloadThread) {
//        downloadThread.setPriority(Thread.MIN_PRIORITY);
//        skinDownloadExecutor.execute(downloadThread);
//    }
}
