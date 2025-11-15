package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UISliderBox;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.network.UpdateHologramProjectorPacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

public class HologramProjectorAngleSetting extends HologramProjectorBaseSetting {

    private UISliderBox sliderX;
    private UISliderBox sliderY;
    private UISliderBox sliderZ;

    private final HologramProjectorBlockEntity entity;
    private final UpdateHologramProjectorPacket.Field<OpenVector3f> field = UpdateHologramProjectorPacket.Field.ANGLE;

    public HologramProjectorAngleSetting(HologramProjectorBlockEntity entity) {
        super("hologram-projector.angle");
        this.entity = entity;
        this.setFrame(new CGRect(0, 0, 200, 82));
        this.setup();
    }

    private void updateValue(UIControl button) {
        float x = (float) sliderX.value();
        float y = (float) sliderY.value();
        float z = (float) sliderZ.value();
        field.set(entity, new OpenVector3f(x, y, z));
    }

    private void didUpdateValue(UIControl button) {
        float x = (float) sliderX.value();
        float y = (float) sliderY.value();
        float z = (float) sliderZ.value();
        NetworkManager.sendToServer(field.buildPacket(entity, new OpenVector3f(x, y, z)));
    }

    private void setup() {
        var value = field.get(entity);
        sliderX = setupSlider(11, 30, "X: ", value.x());
        sliderY = setupSlider(11, 45, "Y: ", value.y());
        sliderZ = setupSlider(11, 60, "Z: ", value.z());
    }

    private UISliderBox setupSlider(int x, int y, String key, double value) {
        var slider = new UISliderBox(new CGRect(x, y, 178, 10));
        slider.setMinValue(-180);
        slider.setMaxValue(180);
        slider.setFormatter(currentValue -> new NSString(String.format("%s%.2f°", key, currentValue)));
        slider.addTarget(this, UIControl.Event.VALUE_CHANGED, HologramProjectorAngleSetting::updateValue);
        slider.addTarget(this, UIControl.Event.EDITING_DID_END, HologramProjectorAngleSetting::didUpdateValue);
        slider.setValue(value);
        addSubview(slider);
        return slider;
    }
}
