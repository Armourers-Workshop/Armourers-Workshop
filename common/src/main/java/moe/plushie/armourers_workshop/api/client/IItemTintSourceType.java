package moe.plushie.armourers_workshop.api.client;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface IItemTintSourceType<T extends IItemTintSource> {

    IDataMapCodec<T> codec();
}
