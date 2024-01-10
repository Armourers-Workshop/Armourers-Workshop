package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;

@SuppressWarnings("unused")
public class ModItemTags {

    public static final IRegistryKey<IItemTag> SWORDS = skinnable("swords");
    public static final IRegistryKey<IItemTag> SHIELDS = skinnable("shields");
    public static final IRegistryKey<IItemTag> BOWS = skinnable("bows");
    public static final IRegistryKey<IItemTag> TRIDENTS = skinnable("tridents");

    public static final IRegistryKey<IItemTag> PICKAXES = skinnable("pickaxes");
    public static final IRegistryKey<IItemTag> AXES = skinnable("axes");
    public static final IRegistryKey<IItemTag> SHOVELS = skinnable("shovels");
    public static final IRegistryKey<IItemTag> HOES = skinnable("hoes");

    public static final IRegistryKey<IItemTag> BOATS = skinnable("boats");
    public static final IRegistryKey<IItemTag> FISHING_RODS = skinnable("fishing_rods");

    private static IRegistryKey<IItemTag> skinnable(String name) {
        return BuilderManager.getInstance().createItemTagBuilder().build("skinnable/" + name);
    }
}
