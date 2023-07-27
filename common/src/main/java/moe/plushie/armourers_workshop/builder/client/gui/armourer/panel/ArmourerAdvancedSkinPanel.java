package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UISliderBox;
import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ArmourerAdvancedSkinPanel extends ArmourerBaseSkinPanel {

    public ArmourerAdvancedSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    public void init() {
        super.init();
        addLabel(0, 0, new NSString("Translate"));
        addSliderBox(0, 0, 154, 10, -128, 128, "", SkinProperty.TRANSFORM_TRANSLATE_X);
        addSliderBox(0, 0, 154, 10, -128, 128, "", SkinProperty.TRANSFORM_TRANSLATE_Y);
        addSliderBox(0, 0, 154, 10, -128, 128, "", SkinProperty.TRANSFORM_TRANSLATE_Z);

        addLabel(0, 0, new NSString("Rotation"));
        addSliderBox(0, 0, 154, 10, -180, 180, "°", SkinProperty.TRANSFORM_ROTATION_X);
        addSliderBox(0, 0, 154, 10, -180, 180, "°", SkinProperty.TRANSFORM_ROTATION_Y);
        addSliderBox(0, 0, 154, 10, -180, 180, "°", SkinProperty.TRANSFORM_ROTATION_Z);

        addLabel(0, 0, new NSString("Scale"));
        addSliderBox(0, 0, 154, 10, Lists.newArrayList(0.25, 0.5, 1.0, 2.0, 4.0), "", SkinProperty.TRANSFORM_SCALE);
    }

    protected UISliderBox addSliderBox(int x, int y, int width, int height, List<Double> values, String suffix, SkinProperty<Double> property) {
        Function<Double, Integer> transform = Double::intValue;
        UISliderBox slider = addSliderBox(x, y, width, height, 0, values.size(), suffix, property);
        slider.setFormatter(value -> new NSString(String.format("%.2f%s", values.get(transform.apply(value)), suffix)));
        slider.setMaxValue(values.size() - 1);
        slider.setMinValue(0);
        slider.setValue(values.indexOf(skinProperties.get(property)));
        slider.removeTarget(this, UIControl.Event.EDITING_DID_END);
        slider.addTarget(this, UIControl.Event.EDITING_DID_END, (self, box) -> {
            double value = ((UISliderBox) box).value();
            skinProperties.put(property, values.get(transform.apply(value)));
            apply();
        });
        return slider;
    }
}
