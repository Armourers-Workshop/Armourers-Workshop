package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeJS;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.EntityVariableStorageImpl;
import moe.plushie.armourers_workshop.core.utils.LazyOptional;
import moe.plushie.armourers_workshop.init.ModCapabilities;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class EntityDataStorage {

    public static EntityImpl of(Entity entity) {
        return DataContainer.of(entity, EntityImpl::new);
    }

    public static BlockEntityImpl of(BlockEntity entity) {
        return DataContainer.of(entity, BlockEntityImpl::new);
    }

    public static class EntityImpl {

        protected final LazyOptional<SkinWardrobe> wardrobe;
        protected final LazyOptional<SkinWardrobeJS> wardrobeJS;
        protected final LazyOptional<EntityRenderData> renderData;
        protected final LazyOptional<EntityAnimationState> animationState;
        protected final LazyOptional<EntityVariableStorageImpl> variableStorage;

        public EntityImpl(Entity entity) {
            this.wardrobe = LazyOptional.of(() -> ModCapabilities.ENTITY_WARDROBE.get().get(entity));
            this.wardrobeJS = LazyOptional.of(() -> wardrobe.resolve().map(SkinWardrobeJS::new));
            this.renderData = LazyOptional.of(() -> EnvironmentExecutor.callOn(EnvironmentType.CLIENT, () -> () -> new EntityRenderData(entity)));
            this.animationState = LazyOptional.ofNullable(EntityAnimationState::new);
            this.variableStorage = LazyOptional.ofNullable(EntityVariableStorageImpl::new);
        }

        public Optional<SkinWardrobe> getWardrobe() {
            return wardrobe.resolve();
        }

        public Optional<SkinWardrobeJS> getWardrobeJS() {
            return wardrobeJS.resolve();
        }

        public Optional<EntityAnimationState> getAnimationState() {
            return animationState.resolve();
        }

        @Environment(EnvType.CLIENT)
        public Optional<EntityRenderData> getRenderData() {
            return renderData.resolve();
        }

        public Optional<EntityVariableStorageImpl> getVariableStorage() {
            return variableStorage.resolve();
        }
    }

    public static class BlockEntityImpl {

        protected final LazyOptional<BlockEntityRenderData> renderData;
        protected final LazyOptional<BlockEntityAnimationState> animationState;
        protected final LazyOptional<EntityVariableStorageImpl> variableStorage;

        public BlockEntityImpl(BlockEntity entity) {
            this.renderData = LazyOptional.of(() -> EnvironmentExecutor.callOn(EnvironmentType.CLIENT, () -> () -> new BlockEntityRenderData(entity)));
            this.animationState = LazyOptional.ofNullable(BlockEntityAnimationState::new);
            this.variableStorage = LazyOptional.ofNullable(EntityVariableStorageImpl::new);
        }

        public Optional<BlockEntityAnimationState> getAnimationState() {
            return animationState.resolve();
        }

        @Environment(EnvType.CLIENT)
        public Optional<BlockEntityRenderData> getRenderData() {
            return renderData.resolve();
        }

        public Optional<EntityVariableStorageImpl> getVariableStorage() {
            return variableStorage.resolve();
        }
    }
}
