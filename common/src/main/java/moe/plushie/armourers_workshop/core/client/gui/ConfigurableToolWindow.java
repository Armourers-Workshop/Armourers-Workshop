package moe.plushie.armourers_workshop.core.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UISliderBox;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.common.IConfigurableToolProperty;
import moe.plushie.armourers_workshop.core.client.gui.widget.ClientMenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.item.option.BooleanToolProperty;
import moe.plushie.armourers_workshop.core.item.option.IntegerToolProperty;
import moe.plushie.armourers_workshop.core.menu.ContainerMenu;
import moe.plushie.armourers_workshop.core.network.UpdateConfigurableToolPacket;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionHand;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class ConfigurableToolWindow extends MenuWindow<ContainerMenu> {

    private final ArrayList<Pair<IConfigurableToolProperty<?>, UIView>> properties = new ArrayList<>();

    private final OpenInteractionHand hand;
    private final ItemStack itemStack;

    private int contentWidth = 176;
    private int contentHeight = 24; // 24 + n + 8

    public ConfigurableToolWindow(Component title, ArrayList<IConfigurableToolProperty<?>> properties, ItemStack itemStack, OpenInteractionHand hand) {
        super(ClientMenuScreen.createEmptyMenu(), ClientMenuScreen.createEmptyInventory(), new NSString(title));
        this.inventoryView.removeFromSuperview();
        this.setBackgroundView(ModTextures.defaultWindowImage());
        this.hand = hand;
        this.itemStack = itemStack;
        properties.forEach(property -> {
            var view = createOptionView(property);
            if (view != null) {
                this.properties.add(Pair.of(property, view));
                this.contentHeight += view.frame().height() + 8;
                this.addSubview(view);
            }
        });
        this.setFrame(new CGRect(0, 0, 176, contentHeight));
    }

    public Screen asScreen() {
        return new ClientMenuScreen(this, title.component());
    }

    protected void sendToServer() {
        NetworkManager.sendToServer(new UpdateConfigurableToolPacket(hand, itemStack));
    }

    private UIView createOptionView(IConfigurableToolProperty<?> property) {
        var name = NSString.localizedString("toolOptions." + property.name());
        if (property instanceof BooleanToolProperty property1) {
            var checkBox = new UICheckBox(new CGRect(8, contentHeight, contentWidth - 16, 9));
            checkBox.setTitle(name);
            checkBox.setSelected(itemStack.get(property1));
            checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
                boolean value = sender.isSelected();
                itemStack.set(property1, value);
                sendToServer();
            });
            return checkBox;
        }
        if (property instanceof IntegerToolProperty property1) {
            var slider = new UISliderBox(new CGRect(8, contentHeight, contentWidth - 16, 20));
            slider.setFormatter(currentValue -> {
                NSMutableString formattedValue = new NSMutableString("");
                formattedValue.append(name);
                formattedValue.append(" ");
                formattedValue.append(String.format("%.1f", currentValue));
                return formattedValue;
            });
            slider.setSmall(true);
            slider.setMinValue(property1.minValue());
            slider.setMaxValue(property1.maxValue());
            slider.setValue(itemStack.get(property1));
            slider.addTarget(this, UIControl.Event.EDITING_DID_END, (self, sender) -> {
                int value = (int) ((UISliderBox) sender).value();
                itemStack.set(property1, value);
                sendToServer();
            });
            return slider;
        }
        return null;
    }
}
