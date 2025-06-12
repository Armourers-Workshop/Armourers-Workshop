package moe.plushie.armourers_workshop.core.capability;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.data.EntityCollisionContainer;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.NonNullItemList;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SkinWardrobeStorage {

    public static IDataSerializer decoder(Entity entity, CompoundTag inputTag) {
        return new TagSerializer(inputTag, entity);
    }

    public static IDataSerializer encoder(Entity entity, CompoundTag outputTag) {
        return new TagSerializer(outputTag, entity);
    }

    public static void saveDataFixer(SkinWardrobe wardrobe, IDataSerializer serializer) {
        serializer.write(CodingKeys.VERSION, (byte) 1);
    }

    public static void loadDataFixer(SkinWardrobe wardrobe, IDataSerializer serializer) {
        var version = serializer.read(CodingKeys.VERSION);
        if (version <= 0) {
            var inventory = wardrobe.inventory();
            moveSlots(inventory, 67, SkinSlotType.DYE.index(), 16, "align dye slots storage");
            moveSlots(inventory, 57, SkinSlotType.OUTFIT.index(), 10, "align outfit slots storage");
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

    public static void saveBoundingBox(EntityCollisionContainer boundingBox, IDataSerializer serializer) {
        boundingBox.serialize(serializer);
    }

    public static void loadBoundingBox(EntityCollisionContainer boundingBox, SkinWardrobe wardrobe, IDataSerializer serializer) {
        boundingBox.deserialize(serializer);
    }

    public static void saveFlags(BitSet flags, IDataSerializer serializer) {
        var value = 0;
        for (int i = 0; i < 32; ++i) {
            if (flags.get(i)) {
                value |= 1 << i;
            }
        }
        if (value != 0) {
            serializer.write(CodingKeys.VISIBILITY, value);
        }
    }

    public static void loadFlags(BitSet flags, IDataSerializer serializer) {
        var value = serializer.read(CodingKeys.VISIBILITY);
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
            var index = slotType.id() & 0xff;
            var encoded = index << 8 | count & 0xff;
            value.add((short) encoded);
        });
        if (!value.isEmpty()) {
            serializer.write(CodingKeys.SLOTS, value);
        }
    }

    public static void loadSkinSlots(HashMap<SkinSlotType, Integer> slots, IDataSerializer serializer) {
        var value = serializer.read(CodingKeys.SLOTS);
        for (var encoded : value) {
            var slotType = SkinSlotType.byId((encoded >> 8) & 0xff);
            if (slotType != null) {
                slots.put(slotType, encoded & 0xff);
            }
        }
    }



    private static void moveSlots(Container inventory, int src, int dest, int size, String reason) {
        int changes = 0;
        for (int i = size - 1; i >= 0; --i) {
            var itemStack = inventory.getItem(src + i);
            if (!itemStack.isEmpty()) {
                inventory.setItem(src + i, ItemStack.EMPTY);
                inventory.setItem(dest + i, itemStack);
                changes += 1;
            }
        }
        if (changes != 0) {
            ModLog.info("move {} items from {} - {}, to {} - {}, reason: {}", changes, src, src + size, dest, dest + size, reason);
        }
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<Byte> VERSION = IDataSerializerKey.create("DataVersion", IDataCodec.BYTE, (byte) 0);
        public static final IDataSerializerKey<Integer> VISIBILITY = IDataSerializerKey.create("Visibility", IDataCodec.INT, 0);
        public static final IDataSerializerKey<List<Short>> SLOTS = IDataSerializerKey.create("Slots", IDataCodec.SHORT.listOf(), Collections.emptyList());
    }
}
