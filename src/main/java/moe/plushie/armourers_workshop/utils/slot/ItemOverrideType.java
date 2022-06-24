package moe.plushie.armourers_workshop.utils.slot;

import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModTags;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

import javax.annotation.Nullable;

public enum ItemOverrideType {

    SWORD("sword", ModTags.SWORDS),
    SHIELD("shield", ModTags.SHIELDS),
    BOW("bow", ModTags.BOWS),
    TRIDENT("trident", ModTags.TRIDENTS),

    PICKAXE("pickaxe", ModTags.PICKAXES),
    AXE("axe", ModTags.AXES),
    SHOVEL("shovel", ModTags.SHOVELS),
    HOE("hoe", ModTags.HOES),

    ITEM("item", null);

    private final ITag<Item> tag;
    private final String name;

    ItemOverrideType(String name, ITag<Item> tag) {
        this.name = name;
        this.tag = tag;
    }

    @Nullable
    public static ItemOverrideType of(String name) {
        for (ItemOverrideType overrideType : ItemOverrideType.values()) {
            if (overrideType.getName().equals(name)) {
                return overrideType;
            }
        }
        return null;
    }

    public boolean isOverrideItem(Item item) {
        // we first the using the config item overrides.
        if (ModConfig.Common.overrides.contains(name + ":" + item.getRegistryName())) {
            return true;
        }
        // and then using vanilla's tag system.
        return tag != null && item.is(tag);
    }

    public String getName() {
        return name;
    }
}
