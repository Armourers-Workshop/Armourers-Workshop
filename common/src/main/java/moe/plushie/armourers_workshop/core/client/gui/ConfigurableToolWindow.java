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
import moe.plushie.armourers_workshop.core.menu.AbstractContainerMenu;
import moe.plushie.armourers_workshop.core.network.UpdateConfigurableToolPacket;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ConfigurableToolWindow extends MenuWindow<AbstractContainerMenu> {

    private final ArrayList<Pair<IConfigurableToolProperty<?>, UIView>> properties = new ArrayList<>();

    private final InteractionHand hand;
    private final ItemStack itemStack;

    private int contentWidth = 176;
    private int contentHeight = 24; // 24 + n + 8

    public ConfigurableToolWindow(Component title, ArrayList<IConfigurableToolProperty<?>> properties, ItemStack itemStack, InteractionHand hand) {
        super(ClientMenuScreen.getEmptyMenu(), ClientMenuScreen.getEmptyInventory(), new NSString(title));
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

    public Screen asScreen() {
        return new ClientMenuScreen(this, title.component());
    }

    protected void sendToServer() {
        NetworkManager.sendToServer(new UpdateConfigurableToolPacket(hand, itemStack));
    }

    protected NSString getOptionText(String key) {
        return new NSString(TranslateUtils.title("tooloption.armourers_workshop" + "." + key));
    }

    private UIView createOptionView(IConfigurableToolProperty<?> property) {
        if (property instanceof BooleanToolProperty) {
            BooleanToolProperty property1 = (BooleanToolProperty) property;
            UICheckBox checkBox = new UICheckBox(new CGRect(8, contentHeight, contentWidth - 16, 9));
            checkBox.setTitle(getOptionText(property.getName()));
            checkBox.setSelected(property1.get(itemStack));
            checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, sender) -> {
                boolean value = sender.isSelected();
                property1.set(itemStack, value);
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
                property1.set(itemStack, value);
                sendToServer();
            });
            return slider;
        }
        return null;
    }
}
