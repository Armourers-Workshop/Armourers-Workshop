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
    protected void init() {
        super.init();
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_OVERRIDE_CHEST);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_OVERRIDE_ARM_LEFT);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_OVERRIDE_ARM_RIGHT);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_HIDE_OVERLAY_CHEST);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT);
    }
}
