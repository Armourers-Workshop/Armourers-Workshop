package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UISliderBox;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.network.UpdateHologramProjectorPacket;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class HologramProjectorRotationOffsetSetting extends HologramProjectorBaseSetting {

    private UISliderBox sliderX;
    private UISliderBox sliderY;
    private UISliderBox sliderZ;

    private final HologramProjectorBlockEntity entity;
    private final UpdateHologramProjectorPacket.Field<Vector3f> field = UpdateHologramProjectorPacket.Field.ROTATION_OFFSET;

    public HologramProjectorRotationOffsetSetting(HologramProjectorBlockEntity entity) {
        super("hologram-projector.rotationOffset");
        this.entity = entity;
        this.setFrame(new CGRect(0, 0, 200, 92));
        this.setup();
    }

    private void updateValue(UIControl button) {
        float x = (float) sliderX.value();
        float y = (float) sliderY.value();
        float z = (float) sliderZ.value();
        field.set(entity, new Vector3f(x, y, z));
    }

    private void didUpdateValue(UIControl button) {
        float x = (float) sliderX.value();
        float y = (float) sliderY.value();
        float z = (float) sliderZ.value();
        NetworkManager.sendToServer(field.buildPacket(entity, new Vector3f(x, y, z)));
    }

    private void setup() {
        setupOption(11, 75, UpdateHologramProjectorPacket.Field.SHOWS_ROTATION_POINT, "showRotationPoint");
        Vector3f value = field.get(entity);
        sliderX = setupSlider(11, 30, "X: ", value.x());
        sliderY = setupSlider(11, 45, "Y: ", value.y());
        sliderZ = setupSlider(11, 60, "Z: ", value.z());
    }

    private void setupOption(int x, int y, UpdateHologramProjectorPacket.Field<Boolean> field, String key) {
        UICheckBox checkBox = new UICheckBox(new CGRect(x, y, 178, 10));
        checkBox.setTitle(getDisplayText(key));
        checkBox.setSelected(field.get(entity));
        checkBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            UICheckBox checkBox1 = ObjectUtils.unsafeCast(c);
            field.set(entity, checkBox1.isSelected());
        });
        addSubview(checkBox);
    }

    private UISliderBox setupSlider(int x, int y, String key, double value) {
        UISliderBox slider = new UISliderBox(new CGRect(x, y, 178, 10));
        slider.setMinValue(-64);
        slider.setMaxValue(64);
        slider.setFormatter(currentValue -> new NSString(String.format("%s%.0f", key, currentValue)));
        slider.addTarget(this, UIControl.Event.VALUE_CHANGED, HologramProjectorRotationOffsetSetting::updateValue);
        slider.addTarget(this, UIControl.Event.EDITING_DID_END, HologramProjectorRotationOffsetSetting::didUpdateValue);
        slider.setValue(value);
        addSubview(slider);
        return slider;
    }
}
