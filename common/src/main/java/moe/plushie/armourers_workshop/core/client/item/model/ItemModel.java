package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface ItemModel {

    interface Unbaked {

        IDataMapCodec<? extends Unbaked> type();
    }
}
