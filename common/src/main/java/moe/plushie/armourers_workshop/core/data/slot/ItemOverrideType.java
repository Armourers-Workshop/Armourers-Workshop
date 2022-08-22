package moe.plushie.armourers_workshop.core.data.slot;

import moe.plushie.armourers_workshop.api.common.ITagKey;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

public enum ItemOverrideType {

    SWORD("sword", ModItemTags.SWORDS),
    SHIELD("shield", ModItemTags.SHIELDS),
    BOW("bow", ModItemTags.BOWS),
    TRIDENT("trident", ModItemTags.TRIDENTS),

    PICKAXE("pickaxe", ModItemTags.PICKAXES),
    AXE("axe", ModItemTags.AXES),
    SHOVEL("shovel", ModItemTags.SHOVELS),
    HOE("hoe", ModItemTags.HOES),

    ITEM("item", null);

    private final ITagKey<Item> tag;
    private final String name;

    ItemOverrideType(String name, ITagKey<Item> tag) {
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
        ResourceLocation registryName = Registry.ITEM.getKey(item);
        if (ModConfig.Common.overrides.contains(name + ":" + registryName)) {
            return true;
        }
        // and then using vanilla's tag system.
        return tag != null && tag.contains(item);
    }

    public String getName() {
        return name;
    }
}
