package moe.plushie.armourers_workshop.core.utils;

import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NonNullItemList extends NonNullList<ItemStack> implements IDataSerializable.Mutable {

    public NonNullItemList(int size) {
        super(buildDefaultList(size), ItemStack.EMPTY);
    }

    private static List<ItemStack> buildDefaultList(int size) {
        ItemStack[] objects = new ItemStack[size];
        Arrays.fill(objects, ItemStack.EMPTY);
        return Arrays.asList(objects);
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        var size = size();
        var values = new ArrayList<Pair<Byte, ItemStack>>(size);
        for (int i = 0; i < size; ++i) {
            var itemStack = get(i);
            if (!itemStack.isEmpty()) {
                values.add(Pair.of((byte) i, get(i)));
            }
        }
        serializer.write(CodingKeys.SERIALIZER, values);
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        var size = size();
        var values = serializer.read(CodingKeys.SERIALIZER);
        for (var pair : values) {
            var slot = pair.getFirst() & 0xff;
            if (slot < size) {
                set(slot, pair.getSecond());
            }
        }
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<List<Pair<Byte, ItemStack>>> SERIALIZER = IDataSerializerKey.create("Items", IDataCodec.pair(IDataCodec.BYTE.fieldOf("Slot").codec(), ExtraCodecs.ITEM_STACK).listOf(), Collections.emptyList());
    }
}
