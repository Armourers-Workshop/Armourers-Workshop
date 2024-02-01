package moe.plushie.armourers_workshop.core.capability;

import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataFixerUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Objects;

public class SkinWardrobeStorage {

    public static void saveDataFixer(SkinWardrobe wardrobe, CompoundTag nbt) {
        nbt.putByte(Constants.Key.DATA_VERSION, (byte) 1);
    }

    public static void loadDataFixer(SkinWardrobe wardrobe, CompoundTag nbt) {
        int version = nbt.getByte(Constants.Key.DATA_VERSION);
        if (version <= 0) {
            Container inventory = wardrobe.getInventory();
            DataFixerUtils.move(inventory, 67, SkinSlotType.DYE.getIndex(), 16, "align dye slots storage");
            DataFixerUtils.move(inventory, 57, SkinSlotType.OUTFIT.getIndex(), 10, "align outfit slots storage");
        }
    }

    public static void saveInventoryItems(Container inventory, CompoundTag nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < itemStacks.size(); ++i) {
            itemStacks.set(i, inventory.getItem(i));
        }
        ContainerHelper.saveAllItems(nbt, itemStacks);
    }

    public static void loadInventoryItems(Container inventory, CompoundTag nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, itemStacks);
        for (int i = 0; i < itemStacks.size(); ++i) {
            ItemStack newItemStack = itemStacks.get(i);
            ItemStack oldItemStack = inventory.getItem(i);
            if (!Objects.equals(newItemStack, oldItemStack)) {
                inventory.setItem(i, newItemStack);
            }
        }
    }

    public static void saveFlags(BitSet flags, CompoundTag nbt) {
        int value = 0;
        for (int i = 0; i < 32; ++i) {
            if (flags.get(i)) {
                value |= 1 << i;
            }
        }
        if (value != 0) {
            nbt.putInt("Visibility", value);
        }
    }

    public static void loadFlags(BitSet flags, CompoundTag nbt) {
        int value = nbt.getInt("Visibility");
        flags.clear();
        for (int i = 0; i < 32; ++i) {
            int mask = 1 << i;
            if ((value & mask) != 0) {
                flags.set(i);
            }
        }
    }

    public static void saveSkinSlots(HashMap<SkinSlotType, Integer> slots, CompoundTag nbt) {
        if (slots.isEmpty()) {
            return;
        }
        ListTag value = new ListTag();
        slots.forEach((slotType, count) -> {
            int index = slotType.getId() & 0xff;
            int encoded = index << 8 | count & 0xff;
            value.add(ShortTag.valueOf((short) encoded));
        });
        if (!value.isEmpty()) {
            nbt.put("Slots", value);
        }
    }

    public static void loadSkinSlots(HashMap<SkinSlotType, Integer> slots, CompoundTag nbt) {
        ListTag value = nbt.getList("Slots", 2);
        if (value.isEmpty()) {
            return;
        }
        for (int i = 0; i < value.size(); ++i) {
            short encoded = value.getShort(i);
            SkinSlotType slotType = SkinSlotType.byId((encoded >> 8) & 0xff);
            if (slotType != null) {
                slots.put(slotType, encoded & 0xff);
            }
        }
    }
}
