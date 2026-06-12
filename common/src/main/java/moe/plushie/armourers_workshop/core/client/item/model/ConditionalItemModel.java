package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public class ConditionalItemModel implements ItemModel {

    public static class Unbaked implements ItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(
                instance -> instance.group(
                                //ConditionalItemModelProperties.CODEC.forGetter(ConditionalItemModelWrapper::property),
                                ItemModels.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue),
                                ItemModels.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse)
                        )
                        .apply(instance, Unbaked::new)
        );

        //private final ConditionalItemModelProperty property;
        private final ItemModel.Unbaked onTrue;
        private final ItemModel.Unbaked onFalse;

        public Unbaked(ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) {
            this.onTrue = onTrue;
            this.onFalse = onFalse;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public ItemModel.Unbaked onFalse() {
            return onFalse;
        }

        public ItemModel.Unbaked onTrue() {
            return onTrue;
        }
    }
}
