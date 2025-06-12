package moe.plushie.armourers_workshop.core.menu;

import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public enum SkinSlotType {

    HEAD(0, 0, 10, "head", SkinTypes.ARMOR_HEAD),
    CHEST(1, 10, 10, "chest", SkinTypes.ARMOR_CHEST),
    LEGS(2, 20, 10, "legs", SkinTypes.ARMOR_LEGS),
    FEET(3, 30, 10, "feet", SkinTypes.ARMOR_FEET),
    WINGS(4, 40, 10, "wings", SkinTypes.ARMOR_WINGS),

    SWORD(5, 50, 1, "sword", SkinTypes.ITEM_SWORD),
    SHIELD(6, 51, 1, "shield", SkinTypes.ITEM_SHIELD),
    BOW(7, 52, 1, "bow", SkinTypes.ITEM_BOW),
    TRIDENT(14, 57, 1, "trident", SkinTypes.ITEM_TRIDENT),

    PICKAXE(8, 53, 1, "pickaxe", SkinTypes.ITEM_PICKAXE),
    AXE(9, 54, 1, "axe", SkinTypes.ITEM_AXE),
    SHOVEL(10, 55, 1, "shovel", SkinTypes.ITEM_SHOVEL),
    HOE(11, 56, 1, "hoe", SkinTypes.ITEM_HOE),

    OUTFIT(12, 70, 10, "outfit", SkinTypes.OUTFIT),
    DYE(13, 80, 16, "dye", null),

    HORSE(14, 70, 10, "horse", SkinTypes.HORSE), // the horse entity only

    DEFAULT(15, 64, 6, "default", null),
    BACKPACK(16, 96, 10, "backpack", SkinTypes.ITEM_BACKPACK),

    ANY(99, 0, 106, "any", null);

    private final String serializedName;
    private final int id;
    private final int index;
    private final int size;
    private final SkinType skinType;
    public static final Codec<SkinSlotType> CODEC = Codec.STRING.xmap(Helper::decode, Helper::encode);

    SkinSlotType(int id, int index, int size, String serializedName, SkinType skinType) {
        this.id = id;
        this.serializedName = serializedName;
        this.index = index;
        this.size = size;
        this.skinType = skinType;
        Helper.TOTAL_SIZE = Math.max(Helper.TOTAL_SIZE, index + size);
        Helper.NAMED_SLOTS.put(serializedName, this);
        Helper.INDEXED_SLOTS.put(id, this);
    }

    @Nullable
    public static SkinSlotType byId(int id) {
        return Helper.INDEXED_SLOTS.get(id);
    }

    @Nullable
    public static SkinSlotType byName(String name) {
        return Helper.NAMED_SLOTS.get(name);
    }

    public static SkinSlotType byType(SkinType skinType) {
        for (var slotType : SkinSlotType.values()) {
            if (Objects.equals(slotType.skinType, skinType)) {
                return slotType;
            }
        }
        return SkinSlotType.ANY;
    }

    @Nullable
    public static SkinSlotType byItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return null;
        }
        if (itemStack.getItem() instanceof BottleItem) {
            return DYE;
        }
        var descriptor = SkinDescriptor.of(itemStack);
        if (!descriptor.isEmpty()) {
            return byType(descriptor.type());
        }
        return null;
    }

    public static int getMaxSlotSize() {
        return 10;
    }

    public static int getTotalSize() {
        return Helper.TOTAL_SIZE;
    }

    public static SkinPaintType[] getSupportedPaintTypes() {
        return Helper.SLOT_TO_TYPES;
    }

    public static int getDyeSlotIndex(SkinPaintType paintType) {
        int i = 0;
        for (; i < Helper.SLOT_TO_TYPES.length; ++i) {
            if (Helper.SLOT_TO_TYPES[i] == paintType) {
                break;
            }
        }
        return DYE.index() + i;
    }

    public OpenResourceLocation icon() {
        return ModConstants.key("item/slot/" + serializedName);
    }

    public boolean isResizable() {
        return this != DYE && size > 1;
    }

    public boolean isArmor() {
        return skinType.isArmour();
    }

    public int id() {
        return id;
    }

    public int index() {
        return index;
    }

    public int maxSize() {
        return size;
    }

    public String serializedName() {
        return serializedName;
    }

    public SkinType skinType() {
        return skinType;
    }

    private static class Helper {
        static final HashMap<Integer, SkinSlotType> INDEXED_SLOTS = new HashMap<>();
        static final HashMap<String, SkinSlotType> NAMED_SLOTS = new HashMap<>();
        static final SkinPaintType[] SLOT_TO_TYPES = {
                SkinPaintTypes.DYE_1,
                SkinPaintTypes.DYE_2,
                SkinPaintTypes.DYE_3,
                SkinPaintTypes.DYE_4,
                SkinPaintTypes.DYE_5,
                SkinPaintTypes.DYE_6,
                SkinPaintTypes.DYE_7,
                SkinPaintTypes.DYE_8,
                SkinPaintTypes.SKIN,
                SkinPaintTypes.HAIR,
                SkinPaintTypes.EYES,
                SkinPaintTypes.MISC_1,
                SkinPaintTypes.MISC_2,
                SkinPaintTypes.MISC_3,
                SkinPaintTypes.MISC_4
        };
        static int TOTAL_SIZE = 0;

        static SkinSlotType decode(String name) {
            return NAMED_SLOTS.getOrDefault(name, SkinSlotType.ANY);
        }

        static String encode(SkinSlotType type) {
            return type.serializedName();
        }
    }
}
