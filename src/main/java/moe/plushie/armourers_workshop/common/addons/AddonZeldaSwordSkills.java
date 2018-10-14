package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonZeldaSwordSkills extends ModAddon {

    public AddonZeldaSwordSkills() {
        super("zeldaswordskills", "Zelda Sword Skills");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_darknut");
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_kokiri");
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_ordon");
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_giant");
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_biggoron");
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_master");
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_tempered");
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_golden");
        addItemOverride(ItemOverrideType.SWORD, "zss.sword_master_true");
    }
}
