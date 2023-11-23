package moe.plushie.armourers_workshop.api.registry;

import net.minecraft.network.syncher.EntityDataSerializer;

@SuppressWarnings("unused")
public interface IEntitySerializerBuilder<T> extends IEntryBuilder<EntityDataSerializer<T>> {
}
