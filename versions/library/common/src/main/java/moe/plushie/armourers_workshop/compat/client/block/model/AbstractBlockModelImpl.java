package moe.plushie.armourers_workshop.compat.client.block.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface AbstractBlockModelImpl {

    interface Unbaked {

        IDataMapCodec<? extends Unbaked> type();
    }
}
