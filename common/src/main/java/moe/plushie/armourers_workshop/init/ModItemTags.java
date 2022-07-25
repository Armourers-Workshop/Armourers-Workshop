package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final Tag<Item> SWORDS = skinnable("swords");
    public static final Tag<Item> SHIELDS = skinnable("shields");
    public static final Tag<Item> BOWS = skinnable("bows");
    public static final Tag<Item> TRIDENTS = skinnable("tridents");

    public static final Tag<Item> PICKAXES = skinnable("pickaxes");
    public static final Tag<Item> AXES = skinnable("axes");
    public static final Tag<Item> SHOVELS = skinnable("shovels");
    public static final Tag<Item> HOES = skinnable("hoes");

    private static Tag<Item> skinnable(String name) {
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
