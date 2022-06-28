package moe.plushie.armourers_workshop.core.gui.widget;

import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.util.text.ITextComponent;

public class AWHelpButton extends AWImageButton {

    public AWHelpButton(int x, int y, int width, int height, IPressable pressable, ITooltip tooltip, ITextComponent title) {
        super(x, y, width, height, 0, 0, RenderUtils.TEX_HELP, pressable, tooltip, title);
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        return false;
    }
}
