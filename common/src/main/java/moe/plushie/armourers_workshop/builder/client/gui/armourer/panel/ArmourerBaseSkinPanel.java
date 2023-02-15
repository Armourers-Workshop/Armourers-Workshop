package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UISliderBox;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;
import java.util.function.Consumer;

@Environment(value = EnvType.CLIENT)
public class ArmourerBaseSkinPanel extends UIView {

    protected final SkinProperties skinProperties;
    protected final ArrayList<AbstractWidget> widgets = new ArrayList<>();
    protected int cursorX = 0;
    protected int cursorY = 0;
    protected Consumer<SkinProperties> applier;

    private final String baseKey;

    public ArmourerBaseSkinPanel(SkinProperties skinProperties) {
        super(CGRect.ZERO);
        this.baseKey = "inventory.armourers_workshop.armourer.skinSettings";
        this.skinProperties = skinProperties;
    }

    public void init() {
        widgets.clear();
        cursorX = 10;
        cursorY = 20;
    }

    public void apply() {
        if (applier != null) {
            applier.accept(skinProperties);
        }
    }

    public Consumer<SkinProperties> getApplier() {
        return applier;
    }

    public void setApplier(Consumer<SkinProperties> applier) {
        this.applier = applier;
    }

    protected UISliderBox addSliderBox(int x, int y, int width, int height, double minValue, double maxValue, String suffix, SkinProperty<Double> property) {
        UISliderBox slider = new UISliderBox(new CGRect(cursorX + x, cursorY + y, width, height));
        slider.setFormatter(value -> {
            String formattedValue = String.format("%.0f%s", value, suffix);
            return new NSString(formattedValue);
        });
        slider.setMaxValue(maxValue);
        slider.setMinValue(minValue);
        slider.setValue(skinProperties.get(property));
        slider.addTarget(this, UIControl.Event.EDITING_DID_END, (self, box) -> {
            double value = ((UISliderBox) box).value();
            skinProperties.put(property, value);
            apply();
        });
        addSubview(slider);
        cursorY += 2;
        return slider;
    }

    protected UICheckBox addCheckBox(int x, int y, SkinProperty<Boolean> property) {
        boolean oldValue = skinProperties.get(property);
        UICheckBox checkBox = new UICheckBox(new CGRect(cursorX + x, cursorY + y, 156 - x, 9));
        checkBox.setTitle(getDisplayText(property.getKey()));
        checkBox.setSelected(oldValue);
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, box) -> {
            boolean value = box.isSelected();
            self.skinProperties.put(property, value);
            apply();
        });
        addSubview(checkBox);
        cursorY += 4;
        return checkBox;
    }

    protected UILabel addLabel(int x, int y, NSString message) {
        UILabel label = new UILabel(new CGRect(cursorX + x, cursorY + y, 156 - x, 9));
        label.setText(message);
        addSubview(label);
        return label;
    }

    @Override
    public void addSubview(UIView view) {
        super.addSubview(view);
        cursorY += view.bounds().getHeight() + 2;
    }

    protected NSString getDisplayText(String key) {
        return new NSString(TranslateUtils.title(baseKey + "." + key));
    }

    protected NSString getDisplayText(String key, Object... objects) {
        return new NSString(TranslateUtils.title(baseKey + "." + key, objects));
    }
}
