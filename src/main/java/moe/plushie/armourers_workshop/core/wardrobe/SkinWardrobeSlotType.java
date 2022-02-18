package moe.plushie.armourers_workshop.core.wardrobe;

import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.AWCore;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public enum SkinWardrobeSlotType {
    HEAD("head", 10, SkinTypes.ARMOR_HEAD),
    CHEST("chest", 10, SkinTypes.ARMOR_CHEST),
    LEGS("legs", 10, SkinTypes.ARMOR_LEGS),
    FEET("feet", 10, SkinTypes.ARMOR_FEET),
    WINGS("wings", 10, SkinTypes.ARMOR_WINGS),

    SWORD("sword", 1, SkinTypes.ITEM_SWORD),
    SHIELD("shield", 1, SkinTypes.ITEM_SHIELD),
    BOW("bow", 1, SkinTypes.ITEM_BOW),

    PICKAXE("pickaxe", 1, SkinTypes.TOOL_PICKAXE),
    AXE("axe", 1, SkinTypes.TOOL_AXE),
    SHOVEL("shovel", 1, SkinTypes.TOOL_SHOVEL),
    HOE("hoe", 1, SkinTypes.TOOL_HOE),

    OUTFIT("outfit", 10, SkinTypes.ARMOR_OUTFIT),
    DYE("dye", 16, null);

    private final String name;
    private final int index;
    private final int size;
    private final ISkinType skinType;

    SkinWardrobeSlotType(String name, int size, ISkinType skinType) {
        this.name = name;
        this.index = Helper.COUNTER.getAndAdd(size);
        this.size = size;
        this.skinType = skinType;
    }

    public ResourceLocation getNoItemIcon() {
        return AWCore.resource("textures/items/slot/" + name + ".png");
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public static SkinWardrobeSlotType of(String name) {
        for (SkinWardrobeSlotType slotType : values()) {
            if (Objects.equals(slotType.name, name)) {
                return slotType;
            }
        }
        return null;
    }

    public static SkinWardrobeSlotType of(ISkinType skinType) {
        for (SkinWardrobeSlotType slotType : values()) {
            if (Objects.equals(slotType.skinType, skinType)) {
                return slotType;
            }
        }
        return null;
    }

    public static SkinWardrobeSlotType of(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return null;
        }
        if (itemStack.getItem() instanceof BottleItem) {
            return DYE;
        }
        if (itemStack.getItem() instanceof SkinItem) {
            SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
            return of(descriptor.getType());
        }
        return null;
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

    private static class Helper {
        static final AtomicInteger COUNTER = new AtomicInteger();

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