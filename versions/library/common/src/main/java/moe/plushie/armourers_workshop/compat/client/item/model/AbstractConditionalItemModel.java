package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

public class AbstractConditionalItemModel implements AbstractItemModelImpl {

    public static class Unbaked implements AbstractItemModelImpl.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(
                instance -> instance.group(
                                //ConditionalItemModelProperties.CODEC.forGetter(ConditionalItemModelWrapper::property),
                                AbstractItemModels.CODEC.fieldOf("on_true").forGetter(Unbaked::onTrue),
                                AbstractItemModels.CODEC.fieldOf("on_false").forGetter(Unbaked::onFalse)
                        )
                        .apply(instance, Unbaked::new)
        );

        //private final ConditionalItemModelProperty property;
        private final AbstractItemModelImpl.Unbaked onTrue;
        private final AbstractItemModelImpl.Unbaked onFalse;

        public Unbaked(AbstractItemModelImpl.Unbaked onTrue, AbstractItemModelImpl.Unbaked onFalse) {
            this.onTrue = onTrue;
            this.onFalse = onFalse;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public AbstractItemModelImpl.Unbaked onFalse() {
            return onFalse;
        }

        public AbstractItemModelImpl.Unbaked onTrue() {
            return onTrue;
        }
    }
}
