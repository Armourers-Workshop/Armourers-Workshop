package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

import java.util.Optional;

public class AbstractSelectItemModel implements AbstractItemModelImpl {

    public static class Unbaked implements AbstractItemModelImpl.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(
                instance -> instance.group(
                                //SelectItemModel.UnbakedSwitch.CODEC.forGetter(it -> it.unbakedSwitch),
                                AbstractItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)
                        )
                        .apply(instance, Unbaked::new)
        );

        //private final SelectItemModel.UnbakedSwitch<?, ?> unbakedSwitch;
        private final Optional<AbstractItemModelImpl.Unbaked> fallback;

        public Unbaked(Optional<AbstractItemModelImpl.Unbaked> fallback) {
            this.fallback = fallback;
        }

        @Override
        public IDataMapCodec<? extends Unbaked> type() {
            return MAP_CODEC;
        }

        public Optional<AbstractItemModelImpl.Unbaked> fallback() {
            return fallback;
        }
    }
}
