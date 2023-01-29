package moe.plushie.armourers_workshop.builder.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UISliderBox;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.BooleanToolProperty;
import moe.plushie.armourers_workshop.builder.item.tooloption.IntegerToolProperty;
import moe.plushie.armourers_workshop.builder.network.UpdatePaintingToolPacket;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.menu.AbstractContainerMenu;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class PaintingToolWindow extends MenuWindow<AbstractContainerMenu> {

    private final ArrayList<Pair<IPaintingToolProperty<?>, UIView>> properties = new ArrayList<>();

    private final InteractionHand hand;
    private final ItemStack itemStack;

    private int contentWidth = 176;
    private int contentHeight = 24; // 24 + n + 8

    public PaintingToolWindow(Component title, ArrayList<IPaintingToolProperty<?>> properties, ItemStack itemStack, InteractionHand hand) {
        super(createMenu(), getInventory(), new NSString(title));
        this.inventoryView.removeFromSuperview();
        this.setBackgroundView(ModTextures.defaultWindowImage());
        this.hand = hand;
        this.itemStack = itemStack;
        properties.forEach(property -> {
            UIView view = createOptionView(property);
            if (view != null) {
                this.properties.add(Pair.of(property, view));
                this.contentHeight += view.frame().getHeight() + 8;
                this.addSubview(view);
            }
        });
        this.setFrame(new CGRect(0, 0, 176, contentHeight));
    }

    private static Inventory getInventory() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            return player.getInventory();
        }
        return null;
    }

    private static AbstractContainerMenu createMenu() {
        return new AbstractContainerMenu(null, 0) {
            @Override
            public boolean stillValid(Player player) {
                return false;
            }
        };
    }

    public Screen asScreen() {
        return new MenuScreen<>(this, menu, getInventory(), title.component());
    }

    protected void sendToServer() {
        NetworkManager.sendToServer(new UpdatePaintingToolPacket(hand, itemStack));
    }

    protected NSString getOptionText(String key) {
        return new NSString(TranslateUtils.title("tooloption.armourers_workshop" + "." + key));
    }

    private UIView createOptionView(IPaintingToolProperty<?> property) {
        if (property instanceof BooleanToolProperty) {
            BooleanToolProperty property1 = (BooleanToolProperty) property;
            UICheckBox checkBox = new UICheckBox(new CGRect(8, contentHeight, contentWidth - 16, 9));
            checkBox.setTitle(getOptionText(property.getName()));
            checkBox.setSelected(property1.get(itemStack));
            checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
                boolean value = sender.isSelected();
                property1.setValue(itemStack, value);
                sendToServer();
            });
            return checkBox;
        }
        if (property instanceof IntegerToolProperty) {
            IntegerToolProperty property1 = (IntegerToolProperty) property;
            NSString title = getOptionText(property.getName());
            UISliderBox slider = new UISliderBox(new CGRect(8, contentHeight, contentWidth - 16, 20));
            slider.setFormatter(currentValue -> {
                NSMutableString formattedValue = new NSMutableString("");
                formattedValue.append(title);
                formattedValue.append(" ");
                formattedValue.append(String.format("%.1f", currentValue));
                return formattedValue;
            });
            slider.setSmall(true);
            slider.setMinValue(property1.getMinValue());
            slider.setMaxValue(property1.getMaxValue());
            slider.setValue(property1.get(itemStack));
            slider.addTarget(this, UIControl.Event.EDITING_DID_END, (self, sender) -> {
                int value = (int) ((UISliderBox) sender).value();
                property1.setValue(itemStack, value);
                sendToServer();
            });
            return slider;
        }
        return null;
    }
}
