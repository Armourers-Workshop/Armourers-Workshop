package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.math.IVector3f;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public interface ILootContextParam<T> {

    ILootContextParam<Entity> THIS_ENTITY = () -> Entity.class;
    ILootContextParam<Player> LAST_DAMAGE_PLAYER = () -> Player.class;
    ILootContextParam<DamageSource> DAMAGE_SOURCE = () -> DamageSource.class;
    ILootContextParam<Entity> KILLER_ENTITY = () -> Entity.class;
    ILootContextParam<Entity> DIRECT_KILLER_ENTITY = () -> Entity.class;
    ILootContextParam<IVector3f> ORIGIN = () -> IVector3f.class;
    ILootContextParam<BlockState> BLOCK_STATE = () -> BlockState.class;
    ILootContextParam<BlockEntity> BLOCK_ENTITY = () -> BlockEntity.class;
    ILootContextParam<ItemStack> TOOL = () -> ItemStack.class;
    ILootContextParam<Float> EXPLOSION_RADIUS = () -> Float.class;

    Class<T> getValueClass();
}
