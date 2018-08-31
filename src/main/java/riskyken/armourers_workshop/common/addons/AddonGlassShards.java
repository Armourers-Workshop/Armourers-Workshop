package riskyken.armourers_workshop.common.addons;

public class AddonGlassShards extends ModAddon {

    public AddonGlassShards() {
        super("glass_shards", "Glass Shards");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "glass_sword");
    }
}
