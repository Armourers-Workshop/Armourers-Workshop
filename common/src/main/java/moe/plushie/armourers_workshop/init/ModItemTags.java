package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;

@SuppressWarnings("unused")
public class ModItemTags {

    public static final IRegistryHolder<IItemTag> SWORDS = skinnable("swords");
    public static final IRegistryHolder<IItemTag> SHIELDS = skinnable("shields");
    public static final IRegistryHolder<IItemTag> BOWS = skinnable("bows");
    public static final IRegistryHolder<IItemTag> TRIDENTS = skinnable("tridents");

    public static final IRegistryHolder<IItemTag> PICKAXES = skinnable("pickaxes");
    public static final IRegistryHolder<IItemTag> AXES = skinnable("axes");
    public static final IRegistryHolder<IItemTag> SHOVELS = skinnable("shovels");
    public static final IRegistryHolder<IItemTag> HOES = skinnable("hoes");

    public static final IRegistryHolder<IItemTag> BOATS = skinnable("boats");
    public static final IRegistryHolder<IItemTag> MINECARTS = skinnable("minecarts");
    public static final IRegistryHolder<IItemTag> FISHING_RODS = skinnable("fishing_rods");

    public static final IRegistryHolder<IItemTag> HORSE_ARMORS = skinnable("horse_armors");

    private static IRegistryHolder<IItemTag> skinnable(String name) {
        return BuilderManager.getInstance().createItemTagBuilder().build("skinnable/" + name);
    }
}
