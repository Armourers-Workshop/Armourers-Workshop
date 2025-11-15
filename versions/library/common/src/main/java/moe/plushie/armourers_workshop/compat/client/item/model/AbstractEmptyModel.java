package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public class AbstractEmptyModel implements AbstractItemModel {

    public static class Unbaked implements AbstractItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.unit(Unbaked::new);

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}


