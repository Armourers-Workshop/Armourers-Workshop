package moe.plushie.armourers_workshop.core.data.slot;

import moe.plushie.armourers_workshop.api.common.IItemTag;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.core.utils.TypedRegistry;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItemMatchers;
import moe.plushie.armourers_workshop.init.ModItemTags;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum ItemOverrideType {

    SWORD("sword", ModItemTags.SWORDS, ModItemMatchers.SWORDS),
    SHIELD("shield", ModItemTags.SHIELDS, ModItemMatchers.SHIELDS),
    BOW("bow", ModItemTags.BOWS, ModItemMatchers.BOWS),
    TRIDENT("trident", ModItemTags.TRIDENTS, ModItemMatchers.TRIDENTS),

    PICKAXE("pickaxe", ModItemTags.PICKAXES, ModItemMatchers.PICKAXES),
    AXE("axe", ModItemTags.AXES, ModItemMatchers.AXES),
    SHOVEL("shovel", ModItemTags.SHOVELS, ModItemMatchers.SHOVELS),
    HOE("hoe", ModItemTags.HOES, ModItemMatchers.HOES),

    BOAT("boat", ModItemTags.BOATS, null),
    MINECART("minecart", ModItemTags.MINECARTS, null),

    FISHING_ROD("fishing_rod", ModItemTags.FISHING_RODS, null),
    BACKPACK("backpack", ModItemTags.BACKPACKS, null),

    HORSE_ARMOR("horse_armor", ModItemTags.HORSE_ARMORS, null),

    ITEM("item", null, null);

    private final IRegistryHolder<IItemTag> tag;
    private final String serializedName;
    private final ItemMatcher matcher;

    ItemOverrideType(String serializedName, IRegistryHolder<IItemTag> tag, ItemMatcher matcher) {
        this.serializedName = serializedName;
        this.tag = tag;
        this.matcher = matcher;
    }

    @Nullable
    public static ItemOverrideType of(String name) {
        for (var overrideType : ItemOverrideType.values()) {
            if (overrideType.serializedName().equals(name)) {
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
        var registryName = TypedRegistry.findKey(itemStack.getItem());
        if (ModConfig.Common.overrides.contains(serializedName + ":" + registryName)) {
            return true;
        }
        // test by vanilla's tag system.
        if (tag != null && tag.get().contains(itemStack)) {
            return true;
        }
        // test by item id matching system.
        if (matcher != null) {
            return matcher.test(registryName, itemStack);
        }
        return false;
    }

    public String serializedName() {
        return serializedName;
    }
}
