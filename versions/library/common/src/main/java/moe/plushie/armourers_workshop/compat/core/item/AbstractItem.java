package moe.plushie.armourers_workshop.compat.core.item;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

@Available("[1.16, )")
public class AbstractItem extends AbstractItemImpl implements AbstractItemHandler {

    public AbstractItem(Properties properties) {
        super(properties);
    }

    protected OpenInteractionResult abi$use(Level level, Player player, OpenInteractionHand hand) {
        return super.use(level, player, hand, null);
    }

    protected OpenInteractionResult abi$useOn(UseOnContext context) {
        return super.useOn(context, null);
    }

    protected OpenInteractionResult abi$useOnFirst(ItemStack itemStack, UseOnContext context) {
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

    protected void abi$appendModelProperties(BiConsumer<OpenResourceLocation, IItemModelProperty> builder) {
    }

    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.appendHoverText(itemStack, tooltips, context);
    }

    protected int abi$getModelTintColor(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity entity, int layerIndex) {
        return 0xffffffff;
    }

    protected Component abi$getName(ItemStack itemStack) {
        return super.getName(itemStack);
    }

    protected boolean abi$isFoil(ItemStack itemStack) {
        return super.isFoil(itemStack);
    }

    ///  API Implements

    @Override
    public final OpenInteractionResult use(Level level, Player player, OpenInteractionHand hand, Object service) {
        return abi$use(level, player, hand);
    }

    @Override
    public final OpenInteractionResult useOn(UseOnContext context, Object service) {
        return abi$useOn(context);
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
        return abi$useOnFirst(itemStack, context);
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
