package moe.plushie.armourers_workshop.utils.slot;

import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.common.AWCore;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum SkinSlotType {
    HEAD("head", getMaxSlotSize(), SkinTypes.ARMOR_HEAD),
    CHEST("chest", getMaxSlotSize(), SkinTypes.ARMOR_CHEST),
    LEGS("legs", getMaxSlotSize(), SkinTypes.ARMOR_LEGS),
    FEET("feet", getMaxSlotSize(), SkinTypes.ARMOR_FEET),
    WINGS("wings", getMaxSlotSize(), SkinTypes.ARMOR_WINGS),

    SWORD("sword", 1, SkinTypes.ITEM_SWORD),
    SHIELD("shield", 1, SkinTypes.ITEM_SHIELD),
    BOW("bow", 1, SkinTypes.ITEM_BOW),

    PICKAXE("pickaxe", 1, SkinTypes.TOOL_PICKAXE),
    AXE("axe", 1, SkinTypes.TOOL_AXE),
    SHOVEL("shovel", 1, SkinTypes.TOOL_SHOVEL),
    HOE("hoe", 1, SkinTypes.TOOL_HOE),

    OUTFIT("outfit", getMaxSlotSize(), SkinTypes.OUTFIT),
    DYE("dye", 16, null);

    private final String name;
    private final int index;
    private final int size;
    private final ISkinType skinType;

    SkinSlotType(String name, int size, ISkinType skinType) {
        this.name = name;
        this.index = Helper.COUNTER.getAndAdd(size);
        this.size = size;
        this.skinType = skinType;

        Helper.SKIN_TO_SLOT.put(skinType, this);
        Helper.NAME_TO_SLOT.put(name, this);
    }

    @Nullable
    public static SkinSlotType of(String name) {
        return Helper.NAME_TO_SLOT.get(name);
    }

    @Nullable
    public static SkinSlotType of(ISkinType skinType) {
        return Helper.SKIN_TO_SLOT.get(skinType);
    }

    @Nullable
    public static SkinSlotType of(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return null;
        }
        if (itemStack.getItem() instanceof BottleItem) {
            return DYE;
        }
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (!descriptor.isEmpty()) {
            return of(descriptor.getType());
        }
        return null;
    }

    public ResourceLocation getIconSprite() {
        return AWCore.resource("item/slot/" + name);
    }

    public static int getMaxSlotSize() {
        return 10;
    }

    public static int getTotalSize() {
        return Helper.COUNTER.get();
    }

    public static ISkinPaintType[] getDyeSlots() {
        return Helper.SLOT_TO_TYPES;
    }

    public static int getSlotIndex(ISkinPaintType paintType) {
        int i = 0;
        for (; i < Helper.SLOT_TO_TYPES.length; ++i) {
            if (Helper.SLOT_TO_TYPES[i] == paintType) {
                break;
            }
        }
        return i;
    }

    public boolean isResizable() {
        return this != DYE && size > 1;
    }

    public boolean isArmor() {
        return skinType instanceof ISkinArmorType;
    }

    public int getIndex() {
        return index;
    }

    public int getMaxSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public ISkinType getSkinType() {
        return skinType;
    }

    private static class Helper {
        static final AtomicInteger COUNTER = new AtomicInteger();

        static final HashMap<ISkinType, SkinSlotType> SKIN_TO_SLOT = new HashMap<>();
        static final HashMap<String, SkinSlotType> NAME_TO_SLOT = new HashMap<>();

        static final ISkinPaintType[] SLOT_TO_TYPES = {
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
    }
}
