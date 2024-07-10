package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeJS;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.init.ModCapabilities;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.LazyOptional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class EntityDataStorage {

    public static EntityImpl of(Entity entity) {
        return IAssociatedObjectProvider.of(entity, EntityImpl::new);
    }

    public static BlockEntityImpl of(BlockEntity entity) {
        return IAssociatedObjectProvider.of(entity, BlockEntityImpl::new);
    }

    public static class EntityImpl {

        protected final LazyOptional<SkinWardrobe> wardrobe;
        protected final LazyOptional<SkinWardrobeJS> wardrobeJS;
        protected final LazyOptional<EntityRenderData> renderData;
        protected final LazyOptional<EntityActionSet> actionSet;

        public EntityImpl(Entity entity) {
            this.wardrobe = getLazyWardrobe(entity);
            this.wardrobeJS = getLazyWardrobeJS(wardrobe);
            this.renderData = getLazyRenderData(entity);
            this.actionSet = getLazyActionSet(entity);
        }

        public Optional<SkinWardrobe> getWardrobe() {
            return wardrobe.resolve();
        }

        public Optional<SkinWardrobeJS> getWardrobeJS() {
            return wardrobeJS.resolve();
        }

        @Environment(EnvType.CLIENT)
        public Optional<EntityRenderData> getRenderData() {
            return renderData.resolve();
        }

        public Optional<EntityActionSet> getActionSet() {
            return actionSet.resolve();
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

        private static LazyOptional<EntityRenderData> getLazyRenderData(Entity entity) {
            var renderData = EnvironmentExecutor.callOn(EnvironmentType.CLIENT, () -> () -> new EntityRenderData(entity));
            if (renderData.isPresent()) {
                return LazyOptional.of(renderData::get);
            }
            return LazyOptional.empty();
        }

        private static LazyOptional<EntityActionSet> getLazyActionSet(Entity entity) {
            return LazyOptional.of(EntityActionSet::new);
        }
    }

    public static class BlockEntityImpl {

        protected final LazyOptional<BlockEntityRenderData> renderData;

        public BlockEntityImpl(BlockEntity entity) {
            this.renderData = getLazyRenderData(entity);
        }

        @Environment(EnvType.CLIENT)
        public Optional<BlockEntityRenderData> getRenderData() {
            return renderData.resolve();
        }

        private static LazyOptional<BlockEntityRenderData> getLazyRenderData(BlockEntity blockEntity) {
            var renderData = EnvironmentExecutor.callOn(EnvironmentType.CLIENT, () -> () -> new BlockEntityRenderData(blockEntity));
            if (renderData.isPresent()) {
                return LazyOptional.of(renderData::get);
            }
            return LazyOptional.empty();
        }
    }
}
