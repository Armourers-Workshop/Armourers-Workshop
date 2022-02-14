package moe.plushie.armourers_workshop.client.setting;

import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.entity.LivingEntity;

public class ExtraSettingPanel extends BaseSettingPanel {
    public ExtraSettingPanel(LivingEntity entity) {
        super(TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.man_extras"));
    }
}
