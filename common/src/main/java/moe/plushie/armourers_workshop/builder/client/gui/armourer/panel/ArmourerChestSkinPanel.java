package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class ArmourerChestSkinPanel extends ArmourerBaseSkinPanel {

    public ArmourerChestSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    public void init() {
        super.init();
        addCheckBox(0, 0, SkinProperty.MODEL_OVERRIDE_CHEST);
        addCheckBox(0, 0, SkinProperty.MODEL_OVERRIDE_ARM_LEFT);
        addCheckBox(0, 0, SkinProperty.MODEL_OVERRIDE_ARM_RIGHT);
        addCheckBox(0, 0, SkinProperty.MODEL_HIDE_OVERLAY_CHEST);
        addCheckBox(0, 0, SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT);
        addCheckBox(0, 0, SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT);
    }
}
