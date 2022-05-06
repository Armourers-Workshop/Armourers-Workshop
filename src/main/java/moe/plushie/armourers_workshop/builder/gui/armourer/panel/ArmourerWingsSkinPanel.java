package moe.plushie.armourers_workshop.builder.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ArmourerWingsSkinPanel extends ArmourerBaseSkinPanel {

    public ArmourerWingsSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    protected void init() {
        super.init();
        this.addLabel(0, 0, width, 9, getDisplayText("label.idleSpeed"));
        this.addSliderBox(0, 0, 154, 10, 200, 10000, "ms", SkinProperty.WINGS_IDLE_SPEED);
        this.addLabel(0, 0, width, 9, getDisplayText("label.flyingSpeed"));
        this.addSliderBox(0, 0, 154, 10, 200, 10000, "ms", SkinProperty.WINGS_FLYING_SPEED);
        this.addLabel(0, 0, width, 9, getDisplayText("label.maxAngle"));
        this.addSliderBox(0, 0, 154, 10, -180, 180, "\u00b0", SkinProperty.WINGS_MAX_ANGLE);
        this.addLabel(0, 0, width, 9, getDisplayText("label.minAngle"));
        this.addSliderBox(0, 0, 154, 10, -180, 180, "\u00b0", SkinProperty.WINGS_MIN_ANGLE);

        this.addMovementList(0, 2, 50, 16, SkinProperty.WINGS_MOVMENT_TYPE);
    }

    protected AWComboBox addMovementList(int x, int y, int width, int height, SkinProperty<String> property) {
        int selectedIndex = 0;
        SkinProperty.MovementType selectedMovementType = SkinProperty.MovementType.valueOf(skinProperties.get(property));
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (SkinProperty.MovementType movementType : SkinProperty.MovementType.values()) {
            String key = "movmentType.armourers_workshop." + movementType.name().toLowerCase();
            AWComboBox.ComboItem item = new AWComboBox.ComboItem(TranslateUtils.title(key));
            if (movementType == selectedMovementType) {
                selectedIndex = items.size();
            }
            items.add(item);
        }
        AWComboBox comboBox = new AWComboBox(cursorX + x, cursorY + y, width, height, items, selectedIndex, button -> {
            if (button instanceof AWComboBox) {
                int newValue = ((AWComboBox) button).getSelectedIndex();
                SkinProperty.MovementType newMovementType = SkinProperty.MovementType.values()[newValue];
                skinProperties.put(property, newMovementType.name());
                apply();
            }
        });
        addButton(comboBox);
        return comboBox;
    }
}
