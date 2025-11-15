package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeJS;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.EntityVariableStorageImpl;
import moe.plushie.armourers_workshop.core.utils.LazyOptional;
import moe.plushie.armourers_workshop.init.ModCapabilities;
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
        protected final LazyOptional<EntityAnimationState> animationState;
        protected final LazyOptional<EntityVariableStorageImpl> variableStorage;

        public EntityImpl(Entity entity) {
            this.animationState = LazyOptional.ofNullable(EntityAnimationState::new);
            this.variableStorage = LazyOptional.ofNullable(EntityVariableStorageImpl::new);
            // ..
            this.wardrobe = LazyOptional.of(() -> ModCapabilities.ENTITY_WARDROBE.get().get(entity));
            this.wardrobeJS = LazyOptional.of(() -> wardrobe.resolve().map(SkinWardrobeJS::new));
        }

        public Optional<SkinWardrobe> wardrobe() {
            return wardrobe.resolve();
        }

        public Optional<SkinWardrobeJS> wardrobeJS() {
            return wardrobeJS.resolve();
        }

        public Optional<EntityAnimationState> animationState() {
            return animationState.resolve();
        }

        public Optional<EntityVariableStorageImpl> variableStorage() {
            return variableStorage.resolve();
        }
    }

    public static class BlockEntityImpl {

        protected final LazyOptional<BlockEntityAnimationState> animationState;
        protected final LazyOptional<EntityVariableStorageImpl> variableStorage;

        public BlockEntityImpl(BlockEntity entity) {
            this.animationState = LazyOptional.ofNullable(BlockEntityAnimationState::new);
            this.variableStorage = LazyOptional.ofNullable(EntityVariableStorageImpl::new);
        }

        public Optional<BlockEntityAnimationState> animationState() {
            return animationState.resolve();
        }

        public Optional<EntityVariableStorageImpl> variableStorage() {
            return variableStorage.resolve();
        }
    }
}
