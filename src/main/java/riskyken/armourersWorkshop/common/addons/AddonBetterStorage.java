package riskyken.armourersWorkshop.common.addons;

public class AddonBetterStorage extends ModAddon {

    public AddonBetterStorage() {
        super("betterstorage", "BetterStorage");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "cardboardSword");
    }
}
