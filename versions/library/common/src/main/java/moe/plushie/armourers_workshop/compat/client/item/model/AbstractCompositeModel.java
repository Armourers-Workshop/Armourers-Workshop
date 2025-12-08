package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

import java.util.List;

public class AbstractCompositeModel implements AbstractItemModelImpl {

    public static class Unbaked implements AbstractItemModelImpl.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(AbstractItemModels.CODEC.listOf().fieldOf("models").forGetter(Unbaked::models)).apply(instance, Unbaked::new));

        private final List<AbstractItemModelImpl.Unbaked> models;

        public Unbaked(List<AbstractItemModelImpl.Unbaked> models) {
            this.models = models;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public List<AbstractItemModelImpl.Unbaked> models() {
            return models;
        }
    }
}
