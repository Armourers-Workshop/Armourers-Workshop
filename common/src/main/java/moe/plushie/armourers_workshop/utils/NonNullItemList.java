package moe.plushie.armourers_workshop.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.api.data.IDataSerializerProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NonNullItemList extends NonNullList<ItemStack> implements IDataSerializerProvider {

    private static final Codec<Pair<Byte, ItemStack>> CODEC = Codec.pair(DataTypeCodecs.BYTE.fieldOf("Slot").codec(), DataTypeCodecs.ITEM_STACK);
    private static final DataSerializerKey<List<Pair<Byte, ItemStack>>> SERIALIZER_KEY = DataSerializerKey.create("Items", CODEC.listOf(), Collections.emptyList());

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
        serializer.write(SERIALIZER_KEY, values);
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        var size = size();
        var values = serializer.read(SERIALIZER_KEY);
        for (var pair : values) {
            var slot = pair.getFirst() & 0xff;
            if (slot < size) {
                set(slot, pair.getSecond());
            }
        }
    }
}
