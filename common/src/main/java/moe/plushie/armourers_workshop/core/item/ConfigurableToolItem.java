package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.IConfigurableTool;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.client.gui.ConfigurableToolWindow;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigurableToolItem extends FlavouredItem implements IConfigurableTool {

    public ConfigurableToolItem(Properties properties) {
        super(properties);
    }

    public boolean openContainer(Level level, Player player, OpenInteractionHand hand, ItemStack itemStack) {
        var properties = new ArrayList<IConfigurableToolProperty<?>>();
        createToolProperties(properties::add);
        if (properties.isEmpty()) {
            return false;
        }
        openContainerGUI(getName(itemStack), properties, hand, itemStack);
        return true;
    }

    public void openContainerGUI(Component title, ArrayList<IConfigurableToolProperty<?>> properties, OpenInteractionHand hand, ItemStack itemStack) {
        EnvironmentExecutor.runOnClient(() -> () -> {
            var window = new ConfigurableToolWindow(title, properties, itemStack, hand);
            Minecraft.getInstance().setScreen(window.asScreen());
        });
    }

    protected void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
    }

    protected void appendSettingHoverText(ItemStack itemStack, List<Component> tooltips) {
        tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.openSettings"));
    }

    @Override
    protected OpenInteractionResult abi$use(Level level, Player player, OpenInteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        if (level.isClientSide() && player.isSecondaryUseActive() && openContainer(level, player, hand, itemStack)) {
            return OpenInteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
        }
        return super.abi$use(level, player, hand);
    }

    @Override
    protected void abi$appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.abi$appendHoverText(itemStack, tooltips, context);
        appendColorHoverText(itemStack, tooltips);
        appendSettingHoverText(itemStack, tooltips);
    }
}
