package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public class AbstractBundleSelectedItemSpecialRenderer implements AbstractItemModelImpl {

    public static class Unbaked implements AbstractItemModelImpl.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.unit(Unbaked::new);

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }
}
