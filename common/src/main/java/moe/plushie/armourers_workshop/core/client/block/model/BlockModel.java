package moe.plushie.armourers_workshop.core.client.block.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public interface BlockModel {

    interface Unbaked {

        IDataMapCodec<? extends Unbaked> type();
    }
}
