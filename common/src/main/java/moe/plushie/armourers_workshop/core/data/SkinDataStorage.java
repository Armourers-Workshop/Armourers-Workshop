package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeJS;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.init.ModCapabilities;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.LazyOptional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

import java.util.Optional;

public class SkinDataStorage {

    protected final LazyOptional<SkinWardrobe> wardrobe;
    protected final LazyOptional<SkinWardrobeJS> wardrobeJS;
    protected final LazyOptional<SkinRenderData> renderData;

    public SkinDataStorage(Entity entity) {
        this.wardrobe = getLazyWardrobe(entity);
        this.wardrobeJS = getLazyWardrobeJS(wardrobe);
        this.renderData = getLazyRenderData(entity);
    }

    public static Optional<SkinWardrobe> getWardrobe(Entity entity) {
        var storage = getDataStore(entity);
        return storage.wardrobe.resolve();
    }

    public static Optional<SkinWardrobeJS> getWardrobeJS(Entity entity) {
        var storage = getDataStore(entity);
        return storage.wardrobeJS.resolve();
    }

    @Environment(EnvType.CLIENT)
    public static Optional<SkinRenderData> getRenderData(Entity entity) {
        var storage = getDataStore(entity);
        return storage.renderData.resolve();
    }

    private static SkinDataStorage getDataStore(Entity entity) {
        var provider = (IAssociatedObjectProvider) entity;
        var snapshot = (SkinDataStorage) provider.getAssociatedObject();
        if (snapshot == null) {
            snapshot = new SkinDataStorage(entity);
            provider.setAssociatedObject(snapshot);
        }
        return snapshot;
    }

    private static LazyOptional<SkinWardrobe> getLazyWardrobe(Entity entity) {
        var wardrobe = ModCapabilities.WARDROBE.get().get(entity);
        if (wardrobe.isPresent()) {
            return LazyOptional.of(wardrobe::get);
        }
        return LazyOptional.empty();
    }

    private static LazyOptional<SkinWardrobeJS> getLazyWardrobeJS(LazyOptional<SkinWardrobe> provider) {
        return LazyOptional.of(() -> new SkinWardrobeJS(provider.resolve().orElse(null)));
    }

    private static LazyOptional<SkinRenderData> getLazyRenderData(Entity entity) {
        var renderData = EnvironmentExecutor.callOn(EnvironmentType.CLIENT, () -> () -> new SkinRenderData(entity.getType()));
        if (renderData.isPresent()) {
            return LazyOptional.of(renderData::get);
        }
        return LazyOptional.empty();
    }
}
