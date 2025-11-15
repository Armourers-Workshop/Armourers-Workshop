package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import moe.plushie.armourers_workshop.api.client.IBlockTintSourceType;

@SuppressWarnings("unused")
public interface IBlockTintSourceBuilder<T extends IBlockTintSource> extends IRegistryBuilder<IBlockTintSourceType<T>> {
}
