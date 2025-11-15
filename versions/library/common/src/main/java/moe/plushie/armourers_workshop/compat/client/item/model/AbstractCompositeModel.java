package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

import java.util.List;

public class AbstractCompositeModel implements AbstractItemModel {

    public static class Unbaked implements AbstractItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(AbstractItemModels.CODEC.listOf().fieldOf("models").forGetter(Unbaked::models)).apply(instance, Unbaked::new));

        private final List<AbstractItemModel.Unbaked> models;

        public Unbaked(List<AbstractItemModel.Unbaked> models) {
            this.models = models;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public List<AbstractItemModel.Unbaked> models() {
            return models;
        }
    }
}
