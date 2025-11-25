package moe.plushie.armourers_workshop.compat.core.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.api.common.IUseOnContext;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

@Available("[1.16, )")
public class AbstractBlockItem extends AbstractBlockItemImpl implements AbstractItemHandler {

    public AbstractBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    protected OpenInteractionResult abi$place(BlockPlaceContext context) {
        return super.place(context, null);
    }

    protected OpenInteractionResult abi$use(Level level, Player player, OpenInteractionHand hand) {
        return super.use(level, player, hand, null);
    }

    protected OpenInteractionResult abi$useOn(IUseOnContext context) {
        return super.useOn(AbstractUseOnContext.unwrap(context), null);
    }

    protected OpenInteractionResult abi$useOnFirst(ItemStack itemStack, IUseOnContext context) {
        return OpenInteractionResult.PASS;
    }

    protected OpenInteractionResult abi$attackLivingEntity(ItemStack itemStack, Player player, Entity entity) {
        return OpenInteractionResult.PASS;
    }

    protected OpenInteractionResult abi$interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, OpenInteractionHand hand) {
        return OpenInteractionResult.PASS;
    }

    protected void abi$fill(List<ItemStack> displayItems, CreativeModeTab tab) {
        displayItems.add(getDefaultInstance());
    }

    protected boolean abi$updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        return super.updateCustomBlockEntityTag(pos, level, player, itemStack, blockState);
    }

    protected void abi$appendModelProperties(BiConsumer<OpenResourceLocation, IItemModelProperty> builder) {
    }

    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.appendHoverText(itemStack, tooltips, context);
    }

    protected int abi$getModelTintColor(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity, int layerIndex) {
        return 0xffffffff;
    }

    protected Component abi$getName(ItemStack itemStack) {
        return Component.translatable(abi$getDescriptionId(itemStack));
    }

    protected boolean abi$isFoil(ItemStack itemStack) {
        return super.isFoil(itemStack);
    }

    protected String abi$getDescriptionId(ItemStack itemStack) {
        return getDescriptionId(itemStack);
    }

    ///  API Implements

    @Override
    public final OpenInteractionResult place(BlockPlaceContext context, Object service) {
        return abi$place(context);
    }

    @Override
    public final OpenInteractionResult use(Level level, Player player, OpenInteractionHand hand, Object service) {
        return abi$use(level, player, hand);
    }

    @Override
    public final OpenInteractionResult useOn(UseOnContext context, Object service) {
        return abi$useOn(AbstractUseOnContext.wrap(context));
    }

    @Override
    public final boolean isFoil(ItemStack itemStack) {
        return abi$isFoil(itemStack);
    }

    @Override
    public final Component getName(ItemStack itemStack) {
        return abi$getName(itemStack);
    }

    @Override
    public final boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        return abi$updateCustomBlockEntityTag(pos, level, player, itemStack, blockState);
    }

    @Override
    public final void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        abi$appendHoverText(itemStack, tooltips, context);
    }

    ///  Other

    @Override
    public final OpenInteractionResult attackLivingEntity(ItemStack itemStack, Player player, Entity entity) {
        return abi$attackLivingEntity(itemStack, player, entity);
    }

    @Override
    public final OpenInteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity, OpenInteractionHand hand) {
        return abi$interactLivingEntity(itemStack, player, entity, hand);
    }

    @Override
    public final OpenInteractionResult useOnFirst(ItemStack itemStack, UseOnContext context) {
        return abi$useOnFirst(itemStack, AbstractUseOnContext.wrap(context));
    }

    @Override
    public final void fill(List<ItemStack> displayItems, CreativeModeTab tab) {
        abi$fill(displayItems, tab);
    }

    @Override
    public final void appendModelProperties(BiConsumer<OpenResourceLocation, IItemModelProperty> builder) {
        abi$appendModelProperties(builder);
    }

    @Override
    public final int getModelTintColor(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity, int layerIndex) {
        return abi$getModelTintColor(itemStack, level, entity, layerIndex);
    }
}
