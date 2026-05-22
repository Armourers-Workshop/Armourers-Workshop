package moe.plushie.armourers_workshop.compat.core.block;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ILootBuilder;
import moe.plushie.armourers_workshop.api.common.IRandomSource;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.BiConsumer;

@Available("[16, )")
public abstract class AbstractBlockBehaviour extends Block {

    protected AbstractBlockBehaviour(Properties properties) {
        super(properties);
    }

    // 16-21

    protected abstract BlockState updateShape(BlockState blockState, OpenDirection direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2, Object context);

    protected abstract boolean skipRendering(BlockState blockState, BlockState blockState2, OpenDirection direction, Object context);

    protected abstract OpenInteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context);

    protected abstract OpenInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context);

    protected abstract List<ItemStack> getDrops(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, ILootBuilder builder);

    protected abstract int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, OpenDirection direction, Object context);

    protected abstract void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source);

    protected abstract void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source);

    protected abstract int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction, Object context);

    protected abstract int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, OpenDirection direction, Object context);

    protected abstract BlockState playerWillDestroy(BlockState blockState, Level level, BlockPos blockPos, Player player, Object context);

    protected abstract void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl, Object context);

    protected abstract void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context);

    // 26

    protected abstract void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl, Object context);

    protected abstract void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl, Object context);

    protected abstract void onExplosionHit(BlockState blockState, ServerLevel level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer, Object context);

    protected abstract VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context);

    protected abstract int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context);

    protected abstract void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, Object context);

    protected abstract boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Object context);

    protected abstract ItemStack getCloneItemStack(LevelReader blockGetter, BlockPos blockPos, BlockState blockState, boolean includeData, Object context);
}
