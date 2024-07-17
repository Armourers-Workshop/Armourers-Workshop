package moe.plushie.armourers_workshop.core.capability;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.utils.DataFixerUtils;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import moe.plushie.armourers_workshop.utils.NonNullItemList;
import net.minecraft.world.Container;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SkinWardrobeStorage {

    private static final DataSerializerKey<Byte> VERSION_KEY = DataSerializerKey.create("DataVersion", DataTypeCodecs.BYTE, (byte) 0);
    private static final DataSerializerKey<Integer> VISIBILITY_KEY = DataSerializerKey.create("Visibility", DataTypeCodecs.INT, 0);
    private static final DataSerializerKey<List<Short>> SLOTS_KEY = DataSerializerKey.create("Slots", DataTypeCodecs.SHORT.listOf(), Collections.emptyList());

    public static void saveDataFixer(SkinWardrobe wardrobe, IDataSerializer serializer) {
        serializer.write(VERSION_KEY, (byte) 1);
    }

    public static void loadDataFixer(SkinWardrobe wardrobe, IDataSerializer serializer) {
        var version = serializer.read(VERSION_KEY);
        if (version <= 0) {
            var inventory = wardrobe.getInventory();
            DataFixerUtils.move(inventory, 67, SkinSlotType.DYE.getIndex(), 16, "align dye slots storage");
            DataFixerUtils.move(inventory, 57, SkinSlotType.OUTFIT.getIndex(), 10, "align outfit slots storage");
        }
    }

    public static void saveInventoryItems(Container inventory, IDataSerializer serializer) {
        var itemStacks = new NonNullItemList(inventory.getContainerSize());
        for (int i = 0; i < itemStacks.size(); ++i) {
            itemStacks.set(i, inventory.getItem(i));
        }
        itemStacks.serialize(serializer);
    }

    public static void loadInventoryItems(Container inventory, IDataSerializer serializer) {
        var itemStacks = new NonNullItemList(inventory.getContainerSize());
        itemStacks.deserialize(serializer);
        for (int i = 0; i < itemStacks.size(); ++i) {
            var newItemStack = itemStacks.get(i);
            var oldItemStack = inventory.getItem(i);
            if (!Objects.equals(newItemStack, oldItemStack)) {
                inventory.setItem(i, newItemStack);
            }
        }
    }

    public static void saveFlags(BitSet flags, IDataSerializer serializer) {
        var value = 0;
        for (int i = 0; i < 32; ++i) {
            if (flags.get(i)) {
                value |= 1 << i;
            }
        }
        if (value != 0) {
            serializer.write(VISIBILITY_KEY, value);
        }
    }

    public static void loadFlags(BitSet flags, IDataSerializer serializer) {
        var value = serializer.read(VISIBILITY_KEY);
        flags.clear();
        for (int i = 0; i < 32; ++i) {
            int mask = 1 << i;
            if ((value & mask) != 0) {
                flags.set(i);
            }
        }
    }

    public static void saveSkinSlots(HashMap<SkinSlotType, Integer> slots, IDataSerializer serializer) {
        if (slots.isEmpty()) {
            return;
        }
        var value = new ArrayList<Short>();
        slots.forEach((slotType, count) -> {
            var index = slotType.getId() & 0xff;
            var encoded = index << 8 | count & 0xff;
            value.add((short) encoded);
        });
        if (!value.isEmpty()) {
            serializer.write(SLOTS_KEY, value);
        }
    }

    public static void loadSkinSlots(HashMap<SkinSlotType, Integer> slots, IDataSerializer serializer) {
        var value = serializer.read(SLOTS_KEY);
        for (var encoded : value) {
            var slotType = SkinSlotType.byId((encoded >> 8) & 0xff);
            if (slotType != null) {
                slots.put(slotType, encoded & 0xff);
            }
        }
    }
}
