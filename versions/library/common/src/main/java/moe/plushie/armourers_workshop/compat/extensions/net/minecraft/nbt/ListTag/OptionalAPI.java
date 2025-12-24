package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.nbt.ListTag;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Optional;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.26)")
@Extension
public class OptionalAPI {

    public static Optional<Double> getOptionalDouble(@This ListTag tag, int index) {
        return Optional.of(tag.getDouble(index));
    }

    public static Optional<Float> getOptionalFloat(@This ListTag tag, int index) {
        return Optional.of(tag.getFloat(index));
    }

    public static Optional<Integer> getOptionalInt(@This ListTag tag, int index) {
        return Optional.of(tag.getInt(index));
    }

    public static Optional<Short> getOptionalShort(@This ListTag tag, int index) {
        return Optional.of(tag.getShort(index));
    }

    public static Optional<ListTag> getOptionalList(@This ListTag tag, int index) {
        return Optional.of(tag.getList(index));
    }

    public static Optional<CompoundTag> getOptionalCompound(@This ListTag tag, int index) {
        return Optional.of(tag.getCompound(index));
    }
}
