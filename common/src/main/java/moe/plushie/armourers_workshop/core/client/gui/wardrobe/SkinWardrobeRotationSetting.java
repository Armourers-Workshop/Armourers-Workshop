package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.InputManagerImpl;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UISliderBox;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.gui.widget.EntityPartView;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.StreamUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class SkinWardrobeRotationSetting extends SkinWardrobeBaseSetting {


    private final SkinWardrobe wardrobe;
    private final Entity entity;

    private final EntityPartView partView = new EntityPartView(new CGRect(83, 25, 24, 40));

    private UISliderBox sliderX;
    private UISliderBox sliderY;
    private UISliderBox sliderZ;

    private static int RANDOMLY_INDEX = 0;
    private static ArrayList<HashMap<EntityPartView.Part, Rotations>> RANDOMLY_ROTATIONS;

    public SkinWardrobeRotationSetting(SkinWardrobe wardrobe, Entity entity) {
        super("wardrobe.man_rotations");
        this.wardrobe = wardrobe;
        this.entity = entity;
        this.setup();
        if (RANDOMLY_ROTATIONS == null) {
            RANDOMLY_ROTATIONS = new ArrayList<>();
            loadRandomlyRotations();
        }
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
        var slider = new UISliderBox(new CGRect(x, y, 160, 10));
        slider.setMinValue(-180);
        slider.setMaxValue(180);
        slider.setFormatter(currentValue -> new NSString(String.format("%s%.2f°", key, currentValue)));
        slider.addTarget(this, UIControl.Event.VALUE_CHANGED, SkinWardrobeRotationSetting::updateValue);
        slider.addTarget(this, UIControl.Event.EDITING_DID_END, SkinWardrobeRotationSetting::didUpdateValue);
        addSubview(slider);
        return slider;
    }

    private void setupButton(int x, int y, String key, BiConsumer<SkinWardrobeRotationSetting, UIControl> consumer) {
        var button = new UIButton(new CGRect(x, y, 100, 16));
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
        var rotations = part.getValue(entity);
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
        if (!(entity instanceof MannequinEntity mannequinEntity)) {
            return;
        }
        var nbt = mannequinEntity.saveCustomPose();
        NetworkManager.sendToServer(UpdateWardrobePacket.Field.MANNEQUIN_POSE.buildPacket(wardrobe, nbt));
    }

    private void randomRotation(UIControl button) {
        var parts = getRandomParts();
        for (var part : EntityPartView.Part.values()) {
            var rotations = parts.get(part);
            if (rotations != null) {
                part.setValue(entity, rotations);
            }
        }
        setSelectedPart(selectedPart());
        didUpdateValue(sliderX);
    }

    private void resetRotation(UIControl button) {
        var isCtrl = InputManagerImpl.hasControlDown();
        for (var part : EntityPartView.Part.values()) {
            if (isCtrl) {
                part.setValue(entity, new Rotations(0, 0, 0));
            } else {
                part.setValue(entity, part.defaultValue);
            }
        }
        setSelectedPart(selectedPart());
        didUpdateValue(sliderX);
    }

    private HashMap<EntityPartView.Part, Rotations> getRandomParts() {
        var random = new Random();
        // we get rotations from pre-defined json.
        if (InputManagerImpl.hasControlDown() && !RANDOMLY_ROTATIONS.isEmpty()) {
            int index = RANDOMLY_INDEX;
            RANDOMLY_INDEX = (RANDOMLY_INDEX + 1) % RANDOMLY_ROTATIONS.size();
            return RANDOMLY_ROTATIONS.get(index);
        }
        // we get rotations from generator.
        var parts = new HashMap<EntityPartView.Part, Rotations>();
        for (var part : EntityPartView.Part.values()) {
            if (part == EntityPartView.Part.BODY) {
                continue;
            }
            var x = 0.0f;
            var y = 0.0f;
            var z = 0.0f;
            for (var j = 0; j < 3; j++) {
                x += random.nextFloat() * 60.0f - 30.0f;
                y += random.nextFloat() * 60.0f - 30.0f;
                z += random.nextFloat() * 60.0f - 30.0f;
            }
            parts.put(part, new Rotations(x, y, z));
        }
        return parts;
    }

    private double getAngle(double degree) {
        if (degree <= 180) {
            return degree;
        }
        return degree - 360;
    }

    private void loadRandomlyRotations() {
        var resourceManager = EnvironmentManager.getResourceManager();
        resourceManager.readResources(ModConstants.key("models/entity/mannequin"), s -> s.endsWith(".json"), (location, resource) -> {
            var object = StreamUtils.fromPackObject(resource);
            if (object == null) {
                return;
            }
            var parts = new HashMap<EntityPartView.Part, Rotations>();
            for (var part : EntityPartView.Part.values()) {
                object.get(part.name).ifPresent(it -> {
                    var x = it.at(0).floatValue();
                    var y = it.at(1).floatValue();
                    var z = it.at(2).floatValue();
                    parts.put(part, new Rotations(x, y, z));
                });
            }
            RANDOMLY_ROTATIONS.add(parts);
        });
    }
}
