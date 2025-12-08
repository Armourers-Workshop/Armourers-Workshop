package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface AbstractItemModelImpl {

    interface Unbaked {

        IDataMapCodec<? extends Unbaked> type();
    }
}
