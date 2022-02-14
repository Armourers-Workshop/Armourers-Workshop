package moe.plushie.armourers_workshop.client.setting;

import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.entity.LivingEntity;

public class ContributorSettingPanel extends BaseSettingPanel {
    public ContributorSettingPanel(LivingEntity entity) {
        super(TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.contributor"));
    }
}
