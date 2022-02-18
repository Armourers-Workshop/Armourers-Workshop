package moe.plushie.armourers_workshop.core.gui.wardrobe;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public class BaseSettingPanel extends Screen {

    public int leftPos = 0;
    public int topPos = 0;

    protected BaseSettingPanel(ITextComponent title) {
        super(title);
    }
}
