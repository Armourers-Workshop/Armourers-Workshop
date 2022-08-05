package moe.plushie.armourers_workshop.api.common;

import net.minecraft.nbt.Tag;

/**
 * An interface designed to unify various things in the Minecraft
 * code base that can be serialized to and from a NBT tag.
 */
public interface ITagRepresentable<T extends Tag> {

    T serializeNBT();

    void deserializeNBT(T nbt);
}
