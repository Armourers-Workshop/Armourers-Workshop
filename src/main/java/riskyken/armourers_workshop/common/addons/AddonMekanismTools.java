package riskyken.armourers_workshop.common.addons;

public class AddonMekanismTools extends ModAddon {

    public AddonMekanismTools() {
        super("MekanismTools", "Mekanism Tools");
    }
    
    @Override
    public void preInit() {
        addItemOverride(ItemOverrideType.SWORD, "ObsidianSword");
        addItemOverride(ItemOverrideType.SWORD, "LapisLazuliSword");
        addItemOverride(ItemOverrideType.SWORD, "OsmiumSword");
        addItemOverride(ItemOverrideType.SWORD, "BronzeSword");
        addItemOverride(ItemOverrideType.SWORD, "GlowstoneSword");
        addItemOverride(ItemOverrideType.SWORD, "SteelSword");
    }
}
