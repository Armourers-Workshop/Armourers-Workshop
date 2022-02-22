package moe.plushie.armourers_workshop.core.gui.wardrobe;

import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureSettingPanel extends BaseSettingPanel {
    public TextureSettingPanel(SkinWardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.man_texture");
    }

    void test() {

    }
}
