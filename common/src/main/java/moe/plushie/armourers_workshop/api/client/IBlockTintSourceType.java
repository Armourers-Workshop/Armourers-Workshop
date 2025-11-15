package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface IBlockTintSourceType<T extends IBlockTintSource> {

    IDataMapCodec<T> codec();
}
