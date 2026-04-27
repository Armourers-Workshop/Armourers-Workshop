package moe.plushie.armourers_workshop.compat.core.block;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Patch;
import moe.plushie.armourers_workshop.api.common.ILootBuilder;
import moe.plushie.armourers_workshop.api.common.IRandomSource;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionHand;
import moe.plushie.armourers_workshop.compat.core.AbstractInteractionResult;
import moe.plushie.armourers_workshop.compat.core.AbstractLootParamsBuilder;
import moe.plushie.armourers_workshop.compat.core.AbstractRandomSource;
import moe.plushie.armourers_workshop.compat.core.item.AbstractTooltipContext;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

@Available("[21, 26)")
public class AbstractBlockImpl extends Block {

    private static final FastMapper<OpenInteractionResult, ItemInteractionResult> ITEM_INTERACTION_CONVERTER = FastMapper.builder(OpenInteractionResult.FAIL, ItemInteractionResult.FAIL, it -> {
        it.put(OpenInteractionResult.SUCCESS, ItemInteractionResult.SUCCESS);
        it.put(OpenInteractionResult.CONSUME, ItemInteractionResult.CONSUME);
        //it.put(OpenInteractionResult.CONSUME_PARTIAL, ItemInteractionResult.CONSUME_PARTIAL);
        it.put(OpenInteractionResult.PASS, ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        //it.put(OpenInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION, ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION);
        it.put(OpenInteractionResult.FAIL, ItemInteractionResult.FAIL);
    });


    public AbstractBlockImpl(Properties properties) {
        super(properties);
    }

    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        super.randomTick(blockState, serverLevel, blockPos, AbstractRandomSource.unwrap(source));
    }

    @Override
    public final void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource source) {
        randomTick(blockState, serverLevel, blockPos, AbstractRandomSource.wrap(source));
    }

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, IRandomSource source) {
        super.tick(blockState, serverLevel, blockPos, AbstractRandomSource.unwrap(source));
    }

    @Override
    public final void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource source) {
        tick(blockState, serverLevel, blockPos, AbstractRandomSource.wrap(source));
    }

    public List<ItemStack> getDrops(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, ILootBuilder context) {
        return super.getDrops(blockState, AbstractLootParamsBuilder.unwrap(context));
    }

    @Override
    public final List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return getDrops(blockState, null, null, AbstractLootParamsBuilder.wrap(builder));
    }

    @Patch("called in un-direction version")
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos, Direction direction) {
        return super.getAnalogOutputSignal(blockState, level, blockPos);
    }

    public final int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return getAnalogOutputSignal(blockState, level, blockPos, Direction.NORTH);
    }

    public OpenInteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context) {
        // If user is rewritten, forward it.
        if (blockState.getBlock() != this) {
            return AbstractInteractionResult.wrap(blockState.useWithoutItem(level, player, blockHitResult.withPosition(blockPos)));
        }
        return AbstractInteractionResult.wrap(super.useWithoutItem(blockState, level, blockPos, player, blockHitResult));
    }

    @Override
    protected final InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        return AbstractInteractionResult.unwrap(useWithoutItem(blockState, level, blockPos, player, OpenInteractionHand.MAIN_HAND, blockHitResult, null));
    }

    public OpenInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, OpenInteractionHand interactionHand, BlockHitResult blockHitResult, Object context) {
        var result = super.useItemOn(itemStack, blockState, level, blockPos, player, AbstractInteractionHand.unwrap(interactionHand), blockHitResult);
        return AbstractInteractionResult.wrap(result.result());
    }

    @Override
    public final ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        var result = useItemOn(itemStack, blockState, level, blockPos, player, AbstractInteractionHand.wrap(interactionHand), blockHitResult, null);
        return ITEM_INTERACTION_CONVERTER.getValue(result);
    }

    public BlockState playerWillDestroy(BlockState blockState, Level level, BlockPos blockPos, Player player, Object context) {
        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    public final BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return playerWillDestroy(blockState, level, blockPos, player, null);
    }

    protected void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        var context1 = AbstractTooltipContext.unwrap(context);
        super.appendHoverText(itemStack, context1.context, tooltips, context1.flag);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltips, TooltipFlag tooltipFlag) {
        appendHoverText(itemStack, tooltips, AbstractTooltipContext.wrap(tooltipContext, null, tooltipFlag));
    }
}
