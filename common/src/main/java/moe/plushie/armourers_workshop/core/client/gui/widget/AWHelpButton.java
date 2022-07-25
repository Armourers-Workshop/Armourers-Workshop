package moe.plushie.armourers_workshop.core.client.gui.widget;

import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

@Environment(value = EnvType.CLIENT)
public class AWHelpButton extends AWImageButton {

    public AWHelpButton(int x, int y, int width, int height, Button.OnPress pressable, Button.OnTooltip tooltip, Component title) {
        super(x, y, width, height, 0, 0, RenderUtils.TEX_HELP, pressable, tooltip, title);
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        return false;
    }
}
