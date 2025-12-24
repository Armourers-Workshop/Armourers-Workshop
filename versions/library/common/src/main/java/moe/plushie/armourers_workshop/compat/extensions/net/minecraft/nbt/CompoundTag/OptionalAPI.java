package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.nbt.CompoundTag;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Optional;
import java.util.Set;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.26)")
@Extension
public class OptionalAPI {

    public static Set<String> keySet(@This CompoundTag tag) {
        return tag.getAllKeys();
    }

    public static Optional<String> getOptionalString(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.STRING)) {
            return Optional.of(tag.getString(key));
        }
        return Optional.empty();
    }

    public static Optional<Double> getOptionalDouble(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.ANY_NUMERIC)) {
            return Optional.of(tag.getDouble(key));
        }
        return Optional.empty();
    }

    public static Optional<Float> getOptionalFloat(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.ANY_NUMERIC)) {
            return Optional.of(tag.getFloat(key));
        }
        return Optional.empty();
    }

    public static Optional<Long> getOptionalLong(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.ANY_NUMERIC)) {
            return Optional.of(tag.getLong(key));
        }
        return Optional.empty();
    }

    public static Optional<Integer> getOptionalInt(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.ANY_NUMERIC)) {
            return Optional.of(tag.getInt(key));
        }
        return Optional.empty();
    }

    public static Optional<Short> getOptionalShort(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.ANY_NUMERIC)) {
            return Optional.of(tag.getShort(key));
        }
        return Optional.empty();
    }

    public static Optional<Byte> getOptionalByte(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.ANY_NUMERIC)) {
            return Optional.of(tag.getByte(key));
        }
        return Optional.empty();
    }

    public static Optional<Boolean> getOptionalBoolean(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.ANY_NUMERIC)) {
            return Optional.of(tag.getBoolean(key));
        }
        return Optional.empty();
    }

    public static Optional<ListTag> getOptionalList(@This CompoundTag tag, String key, int type) {
        if (tag.contains(key, Constants.TagFlags.LIST)) {
            return Optional.of(tag.getList(key, type));
        }
        return Optional.empty();
    }

    public static Optional<CompoundTag> getOptionalCompound(@This CompoundTag tag, String key) {
        if (tag.contains(key, Constants.TagFlags.COMPOUND)) {
            return Optional.of(tag.getCompound(key));
        }
        return Optional.empty();
    }
}
