package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;

public class ArmourerHeadSkinPanel extends ArmourerBaseSkinPanel {

    public ArmourerHeadSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    public void init() {
        super.init();
        addCheckBox(0, 0, SkinProperty.OVERRIDE_MODEL_HEAD);
        addCheckBox(0, 0, SkinProperty.OVERRIDE_OVERLAY_HAT);
        addCheckBox(0, 0, SkinProperty.OVERRIDE_EQUIPMENT_HELMET);
    }
}
