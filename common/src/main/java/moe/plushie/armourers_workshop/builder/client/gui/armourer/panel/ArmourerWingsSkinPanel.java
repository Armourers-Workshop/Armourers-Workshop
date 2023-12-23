package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ArmourerWingsSkinPanel extends ArmourerBaseSkinPanel {

    public ArmourerWingsSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    public void init() {
        super.init();
        addLabel(0, 0, getDisplayText("label.idleSpeed"));
        addSliderBox(0, 0, 154, 10, 200, 10000, "ms", SkinProperty.WINGS_IDLE_SPEED);
        addLabel(0, 0, getDisplayText("label.flyingSpeed"));
        addSliderBox(0, 0, 154, 10, 200, 10000, "ms", SkinProperty.WINGS_FLYING_SPEED);
        addLabel(0, 0, getDisplayText("label.maxAngle"));
        addSliderBox(0, 0, 154, 10, -180, 180, "°", SkinProperty.WINGS_MAX_ANGLE);
        addLabel(0, 0, getDisplayText("label.minAngle"));
        addSliderBox(0, 0, 154, 10, -180, 180, "°", SkinProperty.WINGS_MIN_ANGLE);
        addCheckBox(0, 2, SkinProperty.WINGS_MATCHING_POSE);

        addMovementList(0, 2, 50, 16, SkinProperty.WINGS_MOVMENT_TYPE);
    }

    protected UIComboBox addMovementList(int x, int y, int width, int height, SkinProperty<String> property) {
        int selectedIndex = 0;
        SkinProperty.MovementType selectedMovementType = SkinProperty.MovementType.valueOf(skinProperties.get(property));
        ArrayList<UIComboItem> items = new ArrayList<>();
        for (SkinProperty.MovementType movementType : SkinProperty.MovementType.values()) {
            NSString name = NSString.localizedTableString("movmentType", movementType.name().toLowerCase());
            UIComboItem item = new UIComboItem(name);
            if (movementType == selectedMovementType) {
                selectedIndex = items.size();
            }
            items.add(item);
        }
        UIComboBox comboBox = new UIComboBox(new CGRect(cursorX + x, cursorY + y, width, height));
        comboBox.setSelectedIndex(selectedIndex);
        comboBox.reloadData(items);
        comboBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, box) -> {
            int newValue = ((UIComboBox) box).selectedIndex();
            SkinProperty.MovementType newMovementType = SkinProperty.MovementType.values()[newValue];
            self.skinProperties.put(property, newMovementType.name());
            apply();
        });
        addSubview(comboBox);
        return comboBox;
    }
}
