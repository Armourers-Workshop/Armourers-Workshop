package moe.plushie.armourers_workshop.utils.slot;

import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModTags;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

import javax.annotation.Nullable;

//public static final ISkinType ITEM_SWORD = registerItem("sword", 7, ModTags.SWORDS, SkinPartTypes.ITEM_SWORD);
//public static final ISkinType ITEM_SHIELD = registerItem("shield", 8, ModTags.SHIELDS, SkinPartTypes.ITEM_SHIELD);
//public static final ISkinType ITEM_BOW = registerItem("bow", 9, ModTags.BOWS, SkinPartTypes.ITEM_BOW1, SkinPartTypes.ITEM_BOW2, SkinPartTypes.ITEM_BOW3, SkinPartTypes.ITEM_ARROW);
//
//public static final ISkinType TOOL_PICKAXE = registerItem("pickaxe", 10, ModTags.PICKAXES, SkinPartTypes.TOOL_PICKAXE);
//public static final ISkinType TOOL_AXE = registerItem("axe", 11, ModTags.AXES, SkinPartTypes.TOOL_AXE);
//public static final ISkinType TOOL_SHOVEL = registerItem("shovel", 12, ModTags.SHOVELS, SkinPartTypes.TOOL_SHOVEL);
//public static final ISkinType TOOL_HOE = registerItem("hoe", 13, ModTags.HOES, SkinPartTypes.TOOL_HOE);

public enum ItemOverrideType {

    SWORD("sword", ModTags.SWORDS),
    SHIELD("shield", ModTags.SHIELDS),
    BOW("bow", ModTags.BOWS),

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
