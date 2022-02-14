package moe.plushie.armourers_workshop.client.setting;

import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.entity.LivingEntity;

public class TextureSettingPanel extends BaseSettingPanel {
    public TextureSettingPanel(LivingEntity entity) {
        super(TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.man_texture"));
    }
}
