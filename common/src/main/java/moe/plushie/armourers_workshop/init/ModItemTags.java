package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final IItemTagKey<Item> SWORDS = skinnable("swords");
    public static final IItemTagKey<Item> SHIELDS = skinnable("shields");
    public static final IItemTagKey<Item> BOWS = skinnable("bows");
    public static final IItemTagKey<Item> TRIDENTS = skinnable("tridents");

    public static final IItemTagKey<Item> PICKAXES = skinnable("pickaxes");
    public static final IItemTagKey<Item> AXES = skinnable("axes");
    public static final IItemTagKey<Item> SHOVELS = skinnable("shovels");
    public static final IItemTagKey<Item> HOES = skinnable("hoes");

    private static IItemTagKey<Item> skinnable(String name) {
        return BuilderManager.getInstance().createItemTagBuilder().build("skinnable/" + name);
    }

//    public static boolean isToolItem(Item item) {
//        return item.is(PICKAXES) || item.is(AXES) || item.is(SHOVELS) || item.is(HOES);
//    }
//
//    public static boolean isWeaponItem(Item item) {
//        return item.is(SWORDS) || item.is(SHIELDS) || item.is(BOWS);
//    }
}
