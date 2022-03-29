package moe.plushie.armourers_workshop.core.capability;

import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;

public class SkinDataStorage {

    protected LazyOptional<SkinWardrobe> wardrobe;
    protected LazyOptional<SkinRenderData> renderData;

    protected int lastWardrobeTickCount = Integer.MAX_VALUE;
    protected int lastRenderDataTickCount = Integer.MAX_VALUE;

    public SkinDataStorage(Entity entity) {
        this.wardrobe = getLazyWardrobe(entity);
        this.renderData = getLazyRenderData(entity);
    }

    @Nullable
    public static SkinWardrobe getWardrobe(Entity entity) {
        SkinDataStorage storage = getDataStore(entity);
        if (storage.wardrobe != null && !storage.wardrobe.isPresent()) {
            storage.wardrobe = getLazyWardrobe(entity);
        }
        if (storage.wardrobe != null) {
            return storage.wardrobe.resolve().orElse(null);
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static SkinRenderData getRenderData(Entity entity) {
        SkinDataStorage storage = getDataStore(entity);
        if (storage.renderData == null) {
            return null;
        }
        int tickCount = entity.tickCount;
        if (storage.lastRenderDataTickCount != tickCount) {
            storage.lastRenderDataTickCount = tickCount;
            storage.renderData.ifPresent(renderData1 -> renderData1.tick(entity));
        }
        return storage.renderData.resolve().orElse(null);
    }

    private static SkinDataStorage getDataStore(Entity entity) {
        ISkinDataProvider provider = (ISkinDataProvider) entity;
        SkinDataStorage snapshot = provider.getSkinData();
        if (snapshot == null) {
            snapshot = new SkinDataStorage(entity);
            provider.setSkinData(snapshot);
        }
        return snapshot;
    }

    @Nullable
    private static LazyOptional<SkinWardrobe> getLazyWardrobe(Entity entity) {
        LazyOptional<SkinWardrobe> wardrobe = entity.getCapability(SkinWardrobeProvider.WARDROBE_KEY);
        if (wardrobe.isPresent()) {
            return wardrobe;
        }
        return null;
    }

    @Nullable
    private static LazyOptional<SkinRenderData> getLazyRenderData(Entity entity) {
        return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> LazyOptional.of(SkinRenderData::new));
    }
}
