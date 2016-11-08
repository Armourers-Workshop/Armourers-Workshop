package riskyken.armourersWorkshop.common.addons;

public class AddonBotania extends ModAddon {

    public AddonBotania() {
        super("Botania", "Botania");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "manasteelSword");
        addItemOverride(ItemOverrideType.SWORD, "terraSword");
        addItemOverride(ItemOverrideType.SWORD, "elementiumSword");
        addItemOverride(ItemOverrideType.SWORD, "excaliber");
    }
}
