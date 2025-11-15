package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IContextKey;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
@Available("[1.21, 1.22)")
public class AbstractLootContextParams {

    public static final IContextKey<Entity> THIS_ENTITY = wrap(LootContextParams.THIS_ENTITY);
    public static final IContextKey<Player> LAST_DAMAGE_PLAYER = wrap(LootContextParams.LAST_DAMAGE_PLAYER);
    public static final IContextKey<DamageSource> DAMAGE_SOURCE = wrap(LootContextParams.DAMAGE_SOURCE);
    public static final IContextKey<Entity> ATTACKING_ENTITY = wrap(LootContextParams.ATTACKING_ENTITY);
    public static final IContextKey<Entity> DIRECT_ATTACKING_ENTITY = wrap(LootContextParams.DIRECT_ATTACKING_ENTITY);
    public static final IContextKey<Vec3> ORIGIN = wrap(LootContextParams.ORIGIN);
    public static final IContextKey<BlockState> BLOCK_STATE = wrap(LootContextParams.BLOCK_STATE);
    public static final IContextKey<BlockEntity> BLOCK_ENTITY = wrap(LootContextParams.BLOCK_ENTITY);
    public static final IContextKey<ItemStack> TOOL = wrap(LootContextParams.TOOL);
    public static final IContextKey<Float> EXPLOSION_RADIUS = wrap(LootContextParams.EXPLOSION_RADIUS);
    public static final IContextKey<Integer> ENCHANTMENT_LEVEL = wrap(LootContextParams.ENCHANTMENT_LEVEL);
    public static final IContextKey<Boolean> ENCHANTMENT_ACTIVE = wrap(LootContextParams.ENCHANTMENT_ACTIVE);

    public static <T> IContextKey<T> wrap(LootContextParam<T> key) {
        return new Proxy<>(key);
    }

    public static <T> LootContextParam<T> unwrap(IContextKey<T> key) {
        return ((Proxy<T>) key).impl;
    }

    private static class Proxy<T> implements IContextKey<T> {

        private final LootContextParam<T> impl;
        private final OpenResourceLocation registryName;

        private Proxy(LootContextParam<T> impl) {
            this.impl = impl;
            this.registryName = OpenResourceLocation.of(impl.getName());
        }

        @Override
        public OpenResourceLocation registryName() {
            return registryName;
        }
    }
}
