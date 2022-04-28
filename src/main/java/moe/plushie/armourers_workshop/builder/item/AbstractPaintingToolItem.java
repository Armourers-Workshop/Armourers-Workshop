package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.api.painting.IPaintingTool;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.gui.PaintingToolScreen;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.core.item.FlavouredItem;
import moe.plushie.armourers_workshop.core.item.impl.IPaintApplier;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPaintingToolItem extends FlavouredItem implements IPaintingTool, IPaintApplier {

    public AbstractPaintingToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (world.isClientSide() && player.isShiftKeyDown() && openContainer(world, player, hand, itemStack)) {
            return ActionResult.success(itemStack);
        }
        return super.use(world, player, hand);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (applyColor(context)) {
            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        return ActionResultType.PASS;
    }

    public boolean openContainer(World world, PlayerEntity player, Hand hand, ItemStack itemStack) {
        ArrayList<IPaintingToolProperty<?>> properties = new ArrayList<>();
        createToolProperties(properties::add);
        if (properties.isEmpty()) {
            return false;
        }
        ITextComponent title = getName(itemStack);
        PaintingToolScreen screen = new PaintingToolScreen(title, properties, itemStack, hand);
        Minecraft.getInstance().setScreen(screen);
        return true;
    }

    public void appendColorHoverText(ItemStack itemStack, List<ITextComponent> tooltips, ITooltipFlag flags) {
    }

    public void appendSettingHoverText(ItemStack itemStack, List<ITextComponent> tooltips, ITooltipFlag flags) {
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.openSettings"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltips, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, tooltips, flags);
        appendColorHoverText(itemStack, tooltips, flags);
        appendSettingHoverText(itemStack, tooltips, flags);
    }

    @Override
    public boolean isFullMode(World worldIn, BlockPos blockPos, ItemStack itemStack, @Nullable PlayerEntity player) {
        return ToolOptions.FULL_BLOCK_MODE.get(itemStack);
    }
}
