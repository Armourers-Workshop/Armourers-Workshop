package moe.plushie.armourers_workshop.compat.client.entity.state;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.render.state.BlockEntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.RenderState;
import moe.plushie.armourers_workshop.core.client.render.state.RenderStateManager;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.List;

@Available("[16, 26)")
public class AbstractRenderState {

    private static final StorageImpl STORAGE = new StorageImpl();

    public static <S extends EntityRenderState> S create(Entity entity, float f, float g) {
        return createAndExtract(entity, f, g);
    }

    public static <T extends Entity, S extends EntityRenderState> S createAndExtract(T entity, float f, float partialTick) {
        EntityImpl<T, S> impl = STORAGE.getOrCreate(entity);
        if (impl != null) {
            return impl.updateRenderState(entity, partialTick);
        }
        return null;
    }

    public static <T extends BlockEntity, S extends BlockEntityRenderState> S createAndExtract(T entity, float f, float partialTick) {
        BlockEntityImpl<T, S> impl = STORAGE.getOrCreate(entity);
        if (impl != null) {
            return impl.updateRenderState(entity, partialTick);
        }
        return null;
    }

    public static <T extends Entity, S extends EntityRenderState> S wrap(T entity) {
        EntityImpl<T, S> impl = STORAGE.getOrCreate(entity);
        if (impl != null) {
            return impl.updateRenderStateIfNeeded(entity);
        }
        return null;
    }

    public static <T extends BlockEntity, S extends BlockEntityRenderState> S wrap(T entity) {
        BlockEntityImpl<T, S> impl = STORAGE.getOrCreate(entity);
        if (impl != null) {
            return impl.updateRenderStateIfNeeded(entity);
        }
        return null;
    }

    public static <T extends Entity, S extends EntityRenderState> void unwrap(S renderState, Invoker<T> invoker) {
        EntityImpl<T, S> impl = STORAGE.get(renderState);
        if (impl != null) {
            invoker.accept(impl.entity, impl.f, impl.partialTick);
        }
    }

    public static <T extends BlockEntity, S extends BlockEntityRenderState> void unwrap(S renderState, Invoker<T> invoker) {
        BlockEntityImpl<T, S> impl = STORAGE.get(renderState);
        if (impl != null) {
            invoker.accept(impl.entity, impl.f, impl.partialTick);
        }
    }


    public static <T extends Entity, S extends EntityRenderState> void activate(EntityRenderState renderState) {
        EntityImpl<T, S> impl = STORAGE.get(renderState);
        if (impl != null) {
            impl.activate();
        }
    }

    public static <T extends Entity, S extends EntityRenderState> void deactivate(EntityRenderState renderState) {
        EntityImpl<T, S> impl = STORAGE.get(renderState);
        if (impl != null) {
            impl.deactivate();
        }
    }

    public interface Invoker<T> {

        void accept(T entity, float f, float partialTick);
    }

    private static class StorageImpl {

        private final DataContainer.Key<Object> key = DataContainer.key("RenderState");

        public <T> T get(Object owner) {
            if (owner != null) {
                return Objects.unsafeCast(_get(owner));
            }
            return null;
        }

        public <T> T getOrCreate(Object owner) {
            if (owner != null) {
                return Objects.unsafeCast(_getOrCreate(owner));
            }
            return null;
        }

        public <T> void set(Object owner, T value) {
            DataContainer.set(owner, key, value);
        }

        private Object _get(Object owner) {
            return DataContainer.get(owner, key);
        }

        private Object _getOrCreate(Object owner) {
            var value = DataContainer.get(owner, key);
            if (value != null) {
                return value;
            }
            value = _create(owner);
            if (value != null) {
                DataContainer.set(owner, key, value);
                return value;
            }
            return null;
        }

        private AbstractImpl<?, ?> _create(Object owner) {
            if (owner instanceof Entity) {
                return new EntityImpl<>();
            }
            if (owner instanceof BlockEntity) {
                return new BlockEntityImpl<>();
            }
            return null;
        }
    }

    private abstract static class AbstractImpl<T, S extends RenderState> {

        protected float f = 0;
        protected float partialTick = 0;

        protected T entity;
        protected S renderState;
        protected RenderStateManager<T, S> manager;

        public final S updateRenderState(T entity, float partialTick) {
            this.partialTick = partialTick;
            if (this.renderState == null) {
                this.createRenderState(entity);
            }
            this.extractRenderState(entity, partialTick);
            return this.renderState;
        }

        public final S renderState() {
            return renderState;
        }

        protected void createRenderState(T entity) {
            this.entity = entity;
            this.manager = RenderStateManager.create(entity);
            this.renderState = manager.createRenderState(entity);
            // link the impl to the render state.
            STORAGE.set(renderState, this);
        }

        protected void extractRenderState(T entity, float partialTick) {
            renderState.setPartialTick(partialTick);
            renderState.setAnimationTick(TickUtils.animationTick());
            manager.extractRenderState(entity, renderState);
        }
    }

    private static class EntityImpl<T extends Entity, S extends EntityRenderState> extends AbstractImpl<T, S> {

        private static final List<OpenEquipmentSlot> ARMOUR_SLOTS = Collections.immutableList(builder -> {
            builder.add(OpenEquipmentSlot.HEAD);
            builder.add(OpenEquipmentSlot.CHEST);
            builder.add(OpenEquipmentSlot.LEGS);
            builder.add(OpenEquipmentSlot.FEET);
        });

        private int version = -1;
        private final HashMap<OpenEquipmentSlot, ItemStack> disabledEquipmentItems = new HashMap<>();

        protected void activate() {
            //
            if (renderState.isLimitLimbs() && entity instanceof LivingEntity livingEntity) {
                livingEntity.applyLimitLimbs();
            }

            // ..
            for (var slotType : ARMOUR_SLOTS) {
                if (!renderState.shouldRenderEquipment(slotType)) {
                    var oldItemStack = setItem(entity, slotType, ItemStack.EMPTY);
                    disabledEquipmentItems.put(slotType, oldItemStack);
                }
            }
        }

        protected void deactivate() {
            //
            for (var entry : disabledEquipmentItems.entrySet()) {
                var oldItemStack = entry.getValue();
                setItem(entity, entry.getKey(), oldItemStack);
            }

            disabledEquipmentItems.clear();
        }

        @Override
        protected void extractRenderState(T entity, float partialTick) {
            super.extractRenderState(entity, partialTick);
            version = entity.tickCount;
        }

        protected S updateRenderStateIfNeeded(T entity) {
            // we need update it?
            if (renderState == null || version != entity.tickCount) {
                return updateRenderState(entity, partialTick);
            }
            return renderState;
        }

        private static ItemStack setItem(Entity source, OpenEquipmentSlot slotType, ItemStack itemStack) {
            // for the player, using `setItemSlot` will cause play sound.
            if (source instanceof Player player) {
                var oldItemStack = player.getItemBySlot(slotType);
                player.setItemSlotDirect(slotType, itemStack);
                return oldItemStack;
            }
            if (source instanceof LivingEntity livingEntity) {
                var oldItemStack = livingEntity.getItemBySlot(slotType);
                livingEntity.setItemSlot(slotType, itemStack);
                return oldItemStack;
            }
            return itemStack;
        }
    }

    private static class BlockEntityImpl<T extends BlockEntity, S extends BlockEntityRenderState> extends AbstractImpl<T, S> {

        protected S updateRenderStateIfNeeded(T entity) {
            // we need update it?
            if (renderState == null) {
                return updateRenderState(entity, partialTick);
            }
            return renderState;
        }
    }
}
