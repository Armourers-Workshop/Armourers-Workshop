package moe.plushie.armourers_workshop.core.gui.wardrobe;

import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BaseSettingPanel extends Screen {

    public int leftPos = 0;
    public int topPos = 0;

    private final String baseKey;

    protected BaseSettingPanel(String baseKey) {
        super(TranslateUtils.translate(baseKey));
        this.baseKey = baseKey;
    }

    public ITextComponent getDisplayText(String... parts) {
        StringBuilder key1 = new StringBuilder(baseKey);
        for (String part : parts) {
            key1.append(".").append(part);
        }
        return TranslateUtils.translate(key1.toString());
    }
}
