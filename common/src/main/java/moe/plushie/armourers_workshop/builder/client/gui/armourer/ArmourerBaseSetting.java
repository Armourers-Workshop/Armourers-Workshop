package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class ArmourerBaseSetting extends AWTabPanel {

    public ArmourerBaseSetting(String baseKey) {
        super(baseKey);
    }

    public void reloadData() {
    }
}
