package moe.plushie.armourers_workshop.compat.core.block;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ILootBuilder;
import moe.plushie.armourers_workshop.api.common.IRandomSource;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionHand;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.compat.core.AbstractLootParamsBuilder;
import moe.plushie.armourers_workshop.compat.core.AbstractRandomSource;
import moe.plushie.armourers_workshop.compat.core.item.AbstractItemInteractionResult;
import moe.plushie.armourers_workshop.compat.core.item.AbstractTooltipContext;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.function.BiConsumer;

@Available("[21, 26)")
@SuppressWarnings("NullableProblems")
public abstract class AbstractBlockImplA extends AbstractBlockBehaviour {

    public AbstractBlockImplA(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        super.randomTick(blockState, serverLevel, blockPos, AbstractRandomSource.unwrap(source));
    }

    @Override
    protected final void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource source) {
        randomTick(blockState, serverLevel, blockPos, AbstractRandomSource.wrap(source));
    }

    @Override
    protected void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        super.tick(blockState, serverLevel, blockPos, AbstractRandomSource.unwrap(source));
    }

    @Override
    protected final void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource source) {
        tick(blockState, serverLevel, blockPos, AbstractRandomSource.wrap(source));
    }

    @Override
    protected void onExplosionHit(BlockState blockState, ServerLevel level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer, Object context) {
        super.onExplosionHit(blockState, level, blockPos, explosion, biConsumer);
    }

    @Override
    protected final void onExplosionHit(BlockState blockState, Level level, BlockPos blockPos, Explosion explosion, BiConsumer<ItemStack, BlockPos> biConsumer) {
        if (level instanceof ServerLevel serverLevel) {
            onExplosionHit(blockState, serverLevel, blockPos, explosion, biConsumer, null);
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, ILootBuilder context) {
        return super.getDrops(blockState, AbstractLootParamsBuilder.unwrap(context));
    }

    @Override
    public final List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return getDrops(blockState, null, null, AbstractLootParamsBuilder.wrap(builder));
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader blockGetter, BlockPos blockPos, BlockState blockState, boolean includeData, Object context) {
        return super.getCloneItemStack(blockGetter, blockPos, blockState);
    }

    @Override
    public final ItemStack getCloneItemStack(LevelReader blockGetter, BlockPos blockPos, BlockState blockState) {
        return getCloneItemStack(blockGetter, blockPos, blockState, false, null);
    }

    @Override
    protected OpenInteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context) {
        if (blockState.getBlock() != this) {
            return AbstractInteractionResult.wrap(blockState.useWithoutItem(level, player, blockHitResult.withPosition(blockPos)));
        }
        return AbstractInteractionResult.wrap(super.useWithoutItem(blockState, level, blockPos, player, blockHitResult));
    }

    @Override
    protected final InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return AbstractInteractionResult.unwrap(useWithoutItem(blockState, level, blockPos, player, OpenInteractionHand.MAIN_HAND, blockHitResult, null));
    }

    @Override
    protected OpenInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context) {
        return AbstractItemInteractionResult.wrap(super.useItemOn(itemStack, blockState, level, blockPos, player, AbstractInteractionHand.unwrap(interactionHand), blockHitResult));
    }

    @Override
    protected final ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        return AbstractItemInteractionResult.unwrap(useItemOn(itemStack, blockState, level, blockPos, player, AbstractInteractionHand.wrap(interactionHand), blockHitResult, null));
    }

    @Override
    protected BlockState playerWillDestroy(BlockState blockState, Level level, BlockPos blockPos, Player player, Object context) {
        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public final BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return playerWillDestroy(blockState, level, blockPos, player, null);
    }

    @Override
    protected void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl, Object context) {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
    }

    @Override
    protected void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        var context1 = AbstractTooltipContext.unwrap(context);
        super.appendHoverText(itemStack, context1.context, tooltips, context1.flag);
    }

    @Override
    public final void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltips, TooltipFlag tooltipFlag) {
        appendHoverText(itemStack, tooltips, AbstractTooltipContext.wrap(tooltipContext, null, tooltipFlag));
    }
}
