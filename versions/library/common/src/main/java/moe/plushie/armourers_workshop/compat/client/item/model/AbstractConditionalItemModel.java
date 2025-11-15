package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public class AbstractConditionalItemModel implements AbstractItemModel {

    public static class Unbaked implements AbstractItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(
                instance -> instance.group(
                                //ConditionalItemModelProperties.CODEC.forGetter(ConditionalItemModelWrapper::property),
                                AbstractItemModels.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue),
                                AbstractItemModels.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse)
                        )
                        .apply(instance, Unbaked::new)
        );

        //private final ConditionalItemModelProperty property;
        private final AbstractItemModel.Unbaked onTrue;
        private final AbstractItemModel.Unbaked onFalse;

        public Unbaked(AbstractItemModel.Unbaked onTrue, AbstractItemModel.Unbaked onFalse) {
            this.onTrue = onTrue;
            this.onFalse = onFalse;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public AbstractItemModel.Unbaked onFalse() {
            return onFalse;
        }

        public AbstractItemModel.Unbaked onTrue() {
            return onTrue;
        }
    }
}
