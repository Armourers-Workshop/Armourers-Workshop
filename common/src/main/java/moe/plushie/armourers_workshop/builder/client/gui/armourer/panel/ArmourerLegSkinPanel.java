package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ArmourerLegSkinPanel extends ArmourerBaseSkinPanel {

    public ArmourerLegSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    public void init() {
        super.init();
        addCheckBox(0, 0, SkinProperty.OVERRIDE_MODEL_LEFT_LEG);
        addCheckBox(0, 0, SkinProperty.OVERRIDE_MODEL_RIGHT_LEG);
        addCheckBox(0, 0, SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS);
        addCheckBox(0, 0, SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS);
        addCheckBox(0, 0, SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS);
        addCheckBox(0, 0, SkinProperty.LIMIT_LEGS_LIMBS);
    }
}
