package moe.plushie.armourers_workshop.builder.client.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ArmourerHeadSkinPanel extends ArmourerBaseSkinPanel {

    public ArmourerHeadSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    public void init() {
        super.init();
        addCheckBox(0, 0, SkinProperty.MODEL_OVERRIDE_HEAD);
        addCheckBox(0, 0, SkinProperty.MODEL_HIDE_OVERLAY_HEAD);
    }
}
