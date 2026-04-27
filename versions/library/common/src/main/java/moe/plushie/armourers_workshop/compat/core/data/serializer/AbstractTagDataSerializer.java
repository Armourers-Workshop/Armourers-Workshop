package moe.plushie.armourers_workshop.compat.core.data.serializer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.SerializationContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

@Available("[16, )")
public class AbstractTagDataSerializer extends AbstractDataSerializerImpl<Tag> {

    protected final CompoundTag tag;

    public AbstractTagDataSerializer(CompoundTag tag, SerializationContext context) {
        super(NbtOps.INSTANCE, context);
        this.tag = tag;
    }

    @Override
    protected void writeRootValue(Tag value) {
        tag.merge((CompoundTag) value);
    }

    @Override
    protected Tag readRootValue() {
        return tag;
    }

    @Override
    protected boolean hasValue(String key) {
        return tag.contains(key);
    }

    @Override
    protected void writeValue(String key, Tag value) {
        // we need to merge new value into the item.
        tag.put(key, value);
    }

    @Override
    protected Tag readValue(String key) {
        return tag.get(key);
    }

    public CompoundTag tag() {
        return tag;
    }
}
