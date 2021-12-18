package moe.plushie.armourers_workshop.core.tags;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class SkinTags {

    public static final ITag<Item> SWORDS = forge("swords");
    public static final ITag<Item> SHIELDS = forge("shields");
    public static final ITag<Item> BOWS = forge("bows");

    public static final ITag<Item> PICKAXES = forge("pickaxes");
    public static final ITag<Item> AXES = forge("axes");
    public static final ITag<Item> SHOVELS = forge("shovels");
    public static final ITag<Item> HOES = forge("hoes");

    private static ITag<Item> forge(String name) {
        return ItemTags.createOptional(new ResourceLocation("forge", name));
    }
}
