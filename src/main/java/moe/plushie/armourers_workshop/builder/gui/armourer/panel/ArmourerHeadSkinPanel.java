package moe.plushie.armourers_workshop.builder.gui.armourer.panel;

import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmourerHeadSkinPanel extends ArmourerBaseSkinPanel {

    public ArmourerHeadSkinPanel(SkinProperties skinProperties) {
        super(skinProperties);
    }

    @Override
    protected void init() {
        super.init();
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_OVERRIDE_HEAD);
        this.addCheckBox(0, 0, 9, 9, SkinProperty.MODEL_HIDE_OVERLAY_HEAD);
    }
}
