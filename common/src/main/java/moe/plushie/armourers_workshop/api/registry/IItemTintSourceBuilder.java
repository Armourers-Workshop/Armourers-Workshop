package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.client.IItemTintSourceType;

@SuppressWarnings("unused")
public interface IItemTintSourceBuilder<T extends IItemTintSource> extends IRegistryBuilder<IItemTintSourceType<T>> {
}
