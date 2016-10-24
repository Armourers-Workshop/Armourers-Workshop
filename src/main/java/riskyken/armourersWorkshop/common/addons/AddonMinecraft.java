package riskyken.armourersWorkshop.common.addons;

public class AddonMinecraft extends ModAddon {

    public AddonMinecraft() {
        super("minecraft", "Minecraft");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "wooden_sword");
        addItemOverride(ItemOverrideType.SWORD, "stone_sword");
        addItemOverride(ItemOverrideType.SWORD, "iron_sword");
        addItemOverride(ItemOverrideType.SWORD, "golden_sword");
        addItemOverride(ItemOverrideType.SWORD, "diamond_sword");
        
        addItemOverride(ItemOverrideType.BOW, "bow");
        
        addItemOverride(ItemOverrideType.PICKAXE, "wooden_pickaxe");
        addItemOverride(ItemOverrideType.PICKAXE, "stone_pickaxe");
        addItemOverride(ItemOverrideType.PICKAXE, "iron_pickaxe");
        addItemOverride(ItemOverrideType.PICKAXE, "golden_pickaxe");
        addItemOverride(ItemOverrideType.PICKAXE, "diamond_pickaxe");
    }
}
