package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class ArmourerLegSkinPanel extends ArmourerFeetSkinPanel {

    public ArmourerLegSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    protected void init() {
        super.init();
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_LEGS_LIMIT_LIMBS);
    }
}
