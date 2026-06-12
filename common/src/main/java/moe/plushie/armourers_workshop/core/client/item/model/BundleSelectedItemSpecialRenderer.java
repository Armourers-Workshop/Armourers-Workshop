package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public class BundleSelectedItemSpecialRenderer implements ItemModel {

    public static class Unbaked implements ItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.unit(Unbaked::new);

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
