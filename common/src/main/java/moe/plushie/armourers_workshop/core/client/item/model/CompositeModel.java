package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

import java.util.List;

public class CompositeModel implements ItemModel {

    public static class Unbaked implements ItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(ItemModels.CODEC.listOf().fieldOf("models").forGetter(Unbaked::models)).apply(instance, Unbaked::new));

        private final List<ItemModel.Unbaked> models;

        public Unbaked(List<ItemModel.Unbaked> models) {
            this.models = models;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public List<ItemModel.Unbaked> models() {
            return models;
        }
    }
}
