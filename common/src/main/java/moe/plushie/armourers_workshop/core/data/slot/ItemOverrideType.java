package moe.plushie.armourers_workshop.core.data.slot;

import moe.plushie.armourers_workshop.api.common.IItemTagKey;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItemTags;
import moe.plushie.armourers_workshop.utils.ItemMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum ItemOverrideType {

    SWORD("sword", "sword|tachi|katana|dagger", ModItemTags.SWORDS),
    SHIELD("shield", null, ModItemTags.SHIELDS),
    BOW("bow", null, ModItemTags.BOWS),
    TRIDENT("trident", "trident|lance", ModItemTags.TRIDENTS),

    PICKAXE("pickaxe", null, ModItemTags.PICKAXES),
    AXE("axe", "(?<!pick)axe", ModItemTags.AXES),
    SHOVEL("shovel", null, ModItemTags.SHOVELS),
    HOE("hoe", null, ModItemTags.HOES),

    ITEM("item", null, null);

    private final IItemTagKey<Item> tag;
    private final String name;
    private final ItemMatcher matcher;

    ItemOverrideType(String name, String regex, IItemTagKey<Item> tag) {
        this.name = name;
        this.tag = tag;
        this.matcher = new ItemMatcher(name, regex);
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

    public boolean isOverrideItem(ItemStack itemStack) {
        // yep, the item skin override all item stack.
        if (this == ITEM) {
            return true;
        }
        // test by overrides of the config system.
        ResourceLocation registryName = Registries.ITEM.getKey(itemStack.getItem());
        if (ModConfig.Common.overrides.contains(name + ":" + registryName)) {
            return true;
        }
        // test by vanilla's tag system.
        if (tag != null && tag.contains(itemStack)) {
            return true;
        }
        // test by item id matching system.
        return matcher.test(registryName);
    }

    public String getName() {
        return name;
    }
}
