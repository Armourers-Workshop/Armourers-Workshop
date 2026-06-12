package moe.plushie.armourers_workshop.core.client.item.tintsource;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface ItemTintSourceType<T extends ItemTintSource> {

    IDataMapCodec<T> codec();
}
