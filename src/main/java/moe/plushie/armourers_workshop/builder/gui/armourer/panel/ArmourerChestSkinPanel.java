package moe.plushie.armourers_workshop.builder.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
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
