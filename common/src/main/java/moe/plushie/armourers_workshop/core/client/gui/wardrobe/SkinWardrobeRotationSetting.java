package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UISliderBox;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.gui.widget.EntityPartView;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

import java.util.Random;
import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class SkinWardrobeRotationSetting extends SkinWardrobeBaseSetting {

    private final SkinWardrobe wardrobe;
    private final Entity entity;

    private final EntityPartView partView = new EntityPartView(new CGRect(83, 25, 24, 40));

    private UISliderBox sliderX;
    private UISliderBox sliderY;
    private UISliderBox sliderZ;

    public SkinWardrobeRotationSetting(SkinWardrobe wardrobe, Entity entity) {
        super("inventory.armourers_workshop.wardrobe.man_rotations");
        this.wardrobe = wardrobe;
        this.entity = entity;
        this.setup();
    }

    private void setup() {
        partView.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinWardrobeRotationSetting::updateSelectedPart);
        addSubview(partView);

        sliderX = setupSlider(110, 25, "X: ");
        sliderY = setupSlider(110, 36, "Y: ");
        sliderZ = setupSlider(110, 47, "Z: ");

        setupButton(83, 70, "reset", SkinWardrobeRotationSetting::resetRotation);
        setupButton(83, 90, "random", SkinWardrobeRotationSetting::randomRotation);

        setSelectedPart(EntityPartView.Part.BODY);
    }

    private UISliderBox setupSlider(int x, int y, String key) {
        UISliderBox slider = new UISliderBox(new CGRect(x, y, 160, 10));
        slider.setMinValue(-180);
        slider.setMaxValue(180);
        slider.setFormatter(currentValue -> new NSString(String.format("%s%.2f\u00b0", key, currentValue)));
        slider.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinWardrobeRotationSetting::updateValue);
        slider.addTarget(this, UIControl.Event.EDITING_DID_END, SkinWardrobeRotationSetting::didUpdateValue);
        addSubview(slider);
        return slider;
    }

    private void setupButton(int x, int y, String key, BiConsumer<SkinWardrobeRotationSetting, UIControl> consumer) {
        UIButton button = new UIButton(new CGRect(x, y, 100, 16));
        button.setTitle(getDisplayText(key), UIControl.State.ALL);
        button.setTitleColor(UIColor.WHITE, UIControl.State.ALL);
        button.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, consumer);
        addSubview(button);
    }

    public EntityPartView.Part selectedPart() {
        return partView.selectedPart();
    }

    public void setSelectedPart(EntityPartView.Part part) {
        partView.setSelectedPart(part);
        Rotations rotations = part.getValue(entity);
        sliderX.setValue(getAngle(rotations.getX()));
        sliderY.setValue(getAngle(rotations.getY()));
        sliderZ.setValue(getAngle(rotations.getZ()));
    }

    private void updateSelectedPart(UIControl control) {
        setSelectedPart(selectedPart());
    }

    private void updateValue(UIControl button) {
        float x = (float) sliderX.value();
        float y = (float) sliderY.value();
        float z = (float) sliderZ.value();
        selectedPart().setValue(entity, new Rotations(x, y, z));
    }

    private void didUpdateValue(UIControl button) {
        if (!(entity instanceof MannequinEntity)) {
            return;
        }
        CompoundTag nbt = ((MannequinEntity) entity).saveCustomPose();
        UpdateWardrobePacket packet = UpdateWardrobePacket.field(wardrobe, UpdateWardrobePacket.Field.MANNEQUIN_POSE, nbt);
        NetworkManager.sendToServer(packet);
    }

    private void randomRotation(UIControl button) {
        Random random = new Random();
        for (EntityPartView.Part part : EntityPartView.Part.values()) {
            if (part == EntityPartView.Part.BODY) {
                continue;
            }
            float x = 0.0f;
            float y = 0.0f;
            float z = 0.0f;
            for (int j = 0; j < 3; j++) {
                x += random.nextFloat() * 60.0f - 30.0f;
                y += random.nextFloat() * 60.0f - 30.0f;
                z += random.nextFloat() * 60.0f - 30.0f;
            }
            part.setValue(entity, new Rotations(x, y, z));
        }
        setSelectedPart(selectedPart());
        didUpdateValue(sliderX);
    }

    private void resetRotation(UIControl button) {
        boolean isCtrl = Screen.hasControlDown();
        for (EntityPartView.Part part : EntityPartView.Part.values()) {
            if (isCtrl) {
                part.setValue(entity, new Rotations(0, 0, 0));
            } else {
                part.setValue(entity, part.defaultValue);
            }
        }
        setSelectedPart(selectedPart());
        didUpdateValue(sliderX);
    }

    private double getAngle(double degree) {
        if (degree <= 180) {
            return degree;
        }
        return degree - 360;
    }
}
