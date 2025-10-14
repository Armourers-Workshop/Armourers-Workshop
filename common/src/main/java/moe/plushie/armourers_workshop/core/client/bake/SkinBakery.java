package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.core.IResultHandler;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.library.ISkinLibraryListener;
import moe.plushie.armourers_workshop.core.client.other.SkinVertexBufferSource;
import moe.plushie.armourers_workshop.core.data.DataTransformer;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.data.SkinLibraryManager;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
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
            .cleaner(Duration.ofSeconds(30), this::invalidateSkin0)
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
            SkinVertexBufferSource.clearAllCache();
            ModLog.debug("stop bakery");
        }
    }

    public static void clear() {
        if (BAKERY != null) {
            BAKERY.manager.clear();
            SkinVertexBufferSource.clearAllCache();
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
    public BakedSkin loadSkin(Ticket<String> ticket) {
        if (ticket.get().isEmpty()) {
            return null;
        }
        var pair = manager.getOrLoad(ticket);
        if (pair != null) {
            return pair.getKey();
        }
        return null;
    }

    public void loadSkin(Ticket<String> ticket, IResultHandler<BakedSkin> handler) {
        manager.load(ticket, handler);
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
            difference.removedChanges().forEach(it -> invalidateSkin0(it.skinIdentifier()));
            difference.updatedChanges().forEach(it -> invalidateSkin0(it.getKey().skinIdentifier()));
        });
    }

    private void invalidateSkin0(String identifier) {
        ModLog.debug("'{}' => invalidate baked skin", identifier);
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
            complete.abort(exception);
        }
    }

    private void bakeSkin(String identifier, Skin skin, IResultHandler<BakedSkin> complete) {
        ModLog.debug("'{}' => start baking skin", identifier);
        var startTime = System.currentTimeMillis();

        var usedCounter = new SkinUsedCounter();
        var rootParts = new ArrayList<BakedSkinPart>();
        var bakedParts = new ArrayList<BakedSkinPart>();

        var scheme = new SkinPaintScheme();
        var colorInfo = new ColorDescriptor();
        var renderInfo = new BakedRenderInfo();

        eachPart(skin.parts(), null, (parent, part) -> {
            var children = new ArrayList<BakedSkinPart>();
            BakedGeometryQuads.from(part).forEach((partType, partTransform, quads) -> {
                // when has a different part type, it means the skin part was split.
                // for ensure data safety, we need create a blank skin part to manage data.
                var usedPart = part;
                if (usedPart.type() != partType) {
                    usedPart = new SkinPart.Builder(partType).build();
                }
                var bakedPart = new BakedSkinPart(usedPart, new SkinPartTransform(usedPart, partTransform), quads);
                children.add(bakedPart);
                bakedParts.add(bakedPart);
                usedCounter.add(quads.usedCounter());
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
                if (bakedPart.part() == part) {
                    mainChildPart = bakedPart;
                }
            }
            return mainChildPart;
        });

        BakedGeometryQuads.from(skin.paintData()).forEach((partType, partTransform, quads) -> {
            var part = new SkinPart.Builder(partType).build();
            var bakedPart = new BakedSkinPart(part, new SkinPartTransform(part, partTransform), quads);
            bakedPart.setRenderPolygonOffset(20);
            bakedParts.add(bakedPart);
            rootParts.add(bakedPart);
            usedCounter.add(quads.usedCounter());
            usedCounter.addMarkerTotal(bakedPart.markerTotal());
        });

        // we only bake special parts in preview mode.
        if (skin.settings().isPreviewMode()) {
            BakedGeometryQuads.from(skin.previewData()).forEach((partType, partTransform, quads) -> {
                var part = new SkinPart.Builder(partType).build();
                var bakedPart = new BakedSkinPart(part, new SkinPartTransform(part, partTransform), quads);
                bakedPart.setRenderPolygonOffset(bakedParts.size());
                bakedParts.add(bakedPart);
                rootParts.add(bakedPart);
                usedCounter.add(quads.usedCounter());
                usedCounter.addMarkerTotal(bakedPart.markerTotal());
            });
        }

        // collect color info from the all child parts.
        Collections.eachTree(bakedParts, BakedSkinPart::children, bakedPart -> {
            colorInfo.add(bakedPart.colorInfo());
            bakedPart.quads().forEach((renderType, it) -> renderInfo.add(renderType));
        });

        // collect light info from the all child parts.
        if (renderInfo.hasEmissive() && ModConfig.enableDynamicLightHandler()) {
            renderInfo.setLuminance(BakedLuminanceCalculator.apply(bakedParts));
        }

        usedCounter.addPaintType(colorInfo.paintTypes());

        var totalTime = System.currentTimeMillis() - startTime;
//            int index = bakeTimesIndex.getAndIncrement();
//            if (index > bakeTimes.length() - 1) {
//                index = 0;
//                bakeTimesIndex.set(0);
//            }
//            bakeTimes.set(index, (int) totalTime);

        var bakedSkin = new BakedSkin(identifier, skin.type(), rootParts, skin, scheme, colorInfo, renderInfo, usedCounter);
        ModLog.debug("'{}' => accept baked skin, time: {}ms", identifier, totalTime);
        complete.accept(bakedSkin);
        RenderSystem.recordRenderCall(() -> notifyBake(identifier, bakedSkin));

        // if bake speed too fast, will cause system I/O too high.
        if (totalTime < 250) {
            Executors.sleep(100);
        }
    }

    private void notifyBake(String identifier, BakedSkin bakedSkin) {
        listeners.forEach(listener -> listener.didBake(identifier, bakedSkin));
    }

    private void eachPart(Collection<SkinPart> parts, BakedSkinPart parent, BiFunction<BakedSkinPart, SkinPart, BakedSkinPart> consumer) {
        for (var part : parts) {
            var value = consumer.apply(parent, part);
            eachPart(part.children(), value, consumer);
        }
    }

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface IBakeListener {
        void didBake(String identifier, BakedSkin bakedSkin);
    }
}
