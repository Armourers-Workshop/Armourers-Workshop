package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface AbstractItemModel {

    interface Unbaked {

        IDataMapCodec<? extends Unbaked> type();
    }
}
