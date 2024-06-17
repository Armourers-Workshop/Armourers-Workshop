package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableTool;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.client.gui.ConfigurableToolWindow;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigurableToolItem extends FlavouredItem implements IConfigurableTool {

    public ConfigurableToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        if (level.isClientSide() && player.isShiftKeyDown() && openContainer(level, player, hand, itemStack)) {
            return InteractionResultHolder.success(itemStack);
        }
        return super.use(level, player, hand);
    }

    public void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
    }

    public void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.openSettings"));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.appendHoverText(itemStack, tooltips, context);
        appendColorHoverText(itemStack, tooltips);
        appendSettingHoverText(itemStack, tooltips);
    }

    public boolean openContainer(Level level, Player player, InteractionHand hand, ItemStack itemStack) {
        var properties = new ArrayList<IConfigurableToolProperty<?>>();
        createToolProperties(properties::add);
        if (properties.isEmpty()) {
            return false;
        }
        openContainerGUI(getName(itemStack), properties, hand, itemStack);
        return true;
    }

    @Environment(EnvType.CLIENT)
    public void openContainerGUI(Component title, ArrayList<IConfigurableToolProperty<?>> properties, InteractionHand hand, ItemStack itemStack) {
        var window = new ConfigurableToolWindow(title, properties, itemStack, hand);
        Minecraft.getInstance().setScreen(window.asScreen());
    }
}
