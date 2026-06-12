package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.init.platform.Platform;
import net.minecraft.world.item.Item;

@SuppressWarnings("unused")
public class ModItemTags {

    public static final IRegistryHolder<ITagKey<Item>> SWORDS = skinnable("swords");
    public static final IRegistryHolder<ITagKey<Item>> SHIELDS = skinnable("shields");
    public static final IRegistryHolder<ITagKey<Item>> BOWS = skinnable("bows");
    public static final IRegistryHolder<ITagKey<Item>> TRIDENTS = skinnable("tridents");

    public static final IRegistryHolder<ITagKey<Item>> PICKAXES = skinnable("pickaxes");
    public static final IRegistryHolder<ITagKey<Item>> AXES = skinnable("axes");
    public static final IRegistryHolder<ITagKey<Item>> SHOVELS = skinnable("shovels");
    public static final IRegistryHolder<ITagKey<Item>> HOES = skinnable("hoes");

    public static final IRegistryHolder<ITagKey<Item>> BOATS = skinnable("boats");
    public static final IRegistryHolder<ITagKey<Item>> MINECARTS = skinnable("minecarts");
    public static final IRegistryHolder<ITagKey<Item>> FISHING_RODS = skinnable("fishing_rods");
    public static final IRegistryHolder<ITagKey<Item>> BACKPACKS = skinnable("backpacks");

    public static final IRegistryHolder<ITagKey<Item>> HORSE_ARMORS = skinnable("horse_armors");

    private static IRegistryHolder<ITagKey<Item>> skinnable(String name) {
        return Platform.get().common().builder().itemTag().build("skinnable/" + name);
    }

    public static void init() {
    }
}
