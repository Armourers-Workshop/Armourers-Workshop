package moe.plushie.armourers_workshop.common.addons;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;

public class AddonMinecraft extends ModAddon {

    public AddonMinecraft() {
        super("minecraft", "Minecraft");
    }
    
    @Override
    protected boolean setIsModLoaded() {
        return true;
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "wooden_sword");
        addItemOverride(ItemOverrideType.SWORD, "stone_sword");
        addItemOverride(ItemOverrideType.SWORD, "iron_sword");
        addItemOverride(ItemOverrideType.SWORD, "golden_sword");
        addItemOverride(ItemOverrideType.SWORD, "diamond_sword");
        
        addItemOverride(ItemOverrideType.SHIELD, "shield");
        
        addItemOverride(ItemOverrideType.BOW, "bow");
        
        addItemOverride(ItemOverrideType.PICKAXE, "wooden_pickaxe");
        addItemOverride(ItemOverrideType.PICKAXE, "stone_pickaxe");
        addItemOverride(ItemOverrideType.PICKAXE, "iron_pickaxe");
        addItemOverride(ItemOverrideType.PICKAXE, "golden_pickaxe");
        addItemOverride(ItemOverrideType.PICKAXE, "diamond_pickaxe");
        
        addItemOverride(ItemOverrideType.AXE, "wooden_axe");
        addItemOverride(ItemOverrideType.AXE, "stone_axe");
        addItemOverride(ItemOverrideType.AXE, "iron_axe");
        addItemOverride(ItemOverrideType.AXE, "golden_axe");
        addItemOverride(ItemOverrideType.AXE, "diamond_axe");
        
        addItemOverride(ItemOverrideType.SHOVEL, "wooden_shovel");
        addItemOverride(ItemOverrideType.SHOVEL, "stone_shovel");
        addItemOverride(ItemOverrideType.SHOVEL, "iron_shovel");
        addItemOverride(ItemOverrideType.SHOVEL, "golden_shovel");
        addItemOverride(ItemOverrideType.SHOVEL, "diamond_shovel");
        
        addItemOverride(ItemOverrideType.HOE, "wooden_hoe");
        addItemOverride(ItemOverrideType.HOE, "stone_hoe");
        addItemOverride(ItemOverrideType.HOE, "iron_hoe");
        addItemOverride(ItemOverrideType.HOE, "golden_hoe");
        addItemOverride(ItemOverrideType.HOE, "diamond_hoe");
    }
}
