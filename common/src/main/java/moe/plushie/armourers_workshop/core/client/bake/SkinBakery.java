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
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV0;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;

@Environment(EnvType.CLIENT)
public final class SkinBakery implements ISkinLibraryListener {

    private static final SkinBakery EMPTY = new SkinBakery();
    private static SkinBakery BAKERY;

    private final ArrayList<IBakeListener> listeners = new ArrayList<>();
    private final DataTransformer<String, BakedSkin, Skin> manager = new DataTransformer.Builder<String, BakedSkin, Skin>()
            .thread("AW-SKIN-BK", Thread.MIN_PRIORITY)
            .loadCount(ModConfig.Client.modelBakingThreadCount)
            .transformCount(ModConfig.Client.modelBakingThreadCount)
            .loader(this::loadSkin0)
            .transformer(this::bakeSkin0)
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

    public static void clear() {
        if (BAKERY != null) {
            BAKERY.manager.clear();
            SkinVertexBufferBuilder.clearAllCache();
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
        var pair = manager.get(identifier);
        if (pair != null) {
            return pair.getKey();
        }
        return null;
    }

    @Nullable
    public BakedSkin loadSkin(String identifier, Ticket ticket) {
        if (identifier.isEmpty()) {
            return null;
        }
        var pair = manager.getOrLoad(identifier, ticket);
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
            difference.getRemovedChanges().forEach(it -> invalidateSkin0(it.getSkinIdentifier()));
            difference.getUpdatedChanges().forEach(it -> invalidateSkin0(it.getKey().getSkinIdentifier()));
        });
    }

    private void invalidateSkin0(String identifier) {
        SkinLoader.getInstance().removeSkin(identifier);
        manager.remove(identifier);
    }

    private void loadSkin0(String identifier, IResultHandler<Skin> complete) {
        SkinLoader.getInstance().loadSkin(identifier, complete);
    }

    private void bakeSkin0(String identifier, Skin skin, IResultHandler<BakedSkin> complete) {
        try {
            bakeSkin(identifier, skin, complete);
        } catch (Exception exception) {
            exception.printStackTrace();
            complete.throwing(exception);
        }
    }

    private void bakeSkin(String identifier, Skin skin, IResultHandler<BakedSkin> complete) {
        ModLog.debug("'{}' => start baking skin", identifier);
        var startTime = System.currentTimeMillis();

        var usedCounter = new SkinUsedCounter();
        var rootParts = new ArrayList<BakedSkinPart>();
        var bakedParts = new ArrayList<BakedSkinPart>();

        var scheme = new ColorScheme();
        var colorInfo = new ColorDescriptor();

        eachPart(skin.getParts(), null, (parent, part) -> {
            var children = new ArrayList<BakedSkinPart>();
            BakedCubeQuads.from(part).forEach((partType, quads) -> {
                // when has a different part type, it means the skin part was split.
                // for ensure data safety, we need create a blank skin part to manage data.
                var usedPart = part;
                if (usedPart.getType() != partType) {
                    usedPart = new SkinPart(partType, Collections.emptyList(), new SkinCubesV0(0));
                }
                var bakedPart = new BakedSkinPart(usedPart, quads);
                children.add(bakedPart);
                bakedParts.add(bakedPart);
                usedCounter.addFaceTotal(bakedPart.getFaceTotal());
            });
            // a part maybe bake into multiple parts,
            // but we must add sub-parts into main parts.
            BakedSkinPart mainChildPart = null;
            for (var bakedPart : children) {
                if (parent != null) {
                    parent.addPart(bakedPart);
                } else {
                    rootParts.add(bakedPart);
                }
                if (bakedPart.getPart() == part) {
                    mainChildPart = bakedPart;
                }
            }
            usedCounter.add(part.getCubeData().getUsedCounter());
            // part.clearCubeData();
            return mainChildPart;
        });

        BakedCubeQuads.from(skin.getPaintData()).forEach((partType, quads) -> {
            var part = new SkinPart(partType, Collections.emptyList(), new SkinCubesV0(0));
            var bakedPart = new BakedSkinPart(part, quads);
            bakedPart.setRenderPolygonOffset(20);
            bakedParts.add(bakedPart);
            rootParts.add(bakedPart);
        });

        // we only bake special parts in preview mode.
        if (skin.getSettings().isPreviewMode()) {
            BakedCubeQuads.from(skin.getPreviewData()).forEach((partType, quads) -> {
                var part = new SkinPart(partType, Collections.emptyList(), new SkinCubesV0(0));
                var bakedPart = new BakedSkinPart(part, quads);
                bakedPart.setRenderPolygonOffset(bakedParts.size());
                bakedParts.add(bakedPart);
                rootParts.add(bakedPart);
            });
        }

        var partId = 0;
        var iterator = new ArrayList<>(bakedParts);
        while (!iterator.isEmpty()) {
            var bakedPart = iterator.remove(0);
            bakedPart.setId(partId++);
            colorInfo.add(bakedPart.getColorInfo());
            iterator.addAll(0, bakedPart.getChildren());
        }

        usedCounter.addPaints(colorInfo.getPaintTypes());

        var totalTime = System.currentTimeMillis() - startTime;
//            int index = bakeTimesIndex.getAndIncrement();
//            if (index > bakeTimes.length() - 1) {
//                index = 0;
//                bakeTimesIndex.set(0);
//            }
//            bakeTimes.set(index, (int) totalTime);

        var bakedSkin = new BakedSkin(identifier, skin.getType(), rootParts, skin, scheme, colorInfo, usedCounter);
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

    private void eachPart(Collection<SkinPart> parts, BakedSkinPart parent, BiFunction<BakedSkinPart, SkinPart, BakedSkinPart> consumer) {
        for (var part : parts) {
            var value = consumer.apply(parent, part);
            eachPart(part.getParts(), value, consumer);
        }
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface IBakeListener {
        void didBake(String identifier, BakedSkin bakedSkin);
    }
}
