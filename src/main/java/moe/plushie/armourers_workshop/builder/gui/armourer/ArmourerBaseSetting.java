package moe.plushie.armourers_workshop.builder.gui.armourer;

import moe.plushie.armourers_workshop.core.gui.widget.AWTabPanel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmourerBaseSetting extends AWTabPanel {

    public ArmourerBaseSetting(String baseKey) {
        super(baseKey);
    }

    public void reloadData() {
    }
}
