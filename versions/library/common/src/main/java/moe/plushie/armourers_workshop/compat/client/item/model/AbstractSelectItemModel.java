package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

import java.util.Optional;

public class AbstractSelectItemModel implements AbstractItemModel {

    public static class Unbaked implements AbstractItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(
                instance -> instance.group(
                                //SelectItemModel.UnbakedSwitch.CODEC.forGetter(it -> it.unbakedSwitch),
                                AbstractItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)
                        )
                        .apply(instance, Unbaked::new)
        );

        //private final SelectItemModel.UnbakedSwitch<?, ?> unbakedSwitch;
        private final Optional<AbstractItemModel.Unbaked> fallback;

        public Unbaked(Optional<AbstractItemModel.Unbaked> fallback) {
            this.fallback = fallback;
        }

        @Override
        public IDataMapCodec<? extends Unbaked> type() {
            return MAP_CODEC;
        }

        public Optional<AbstractItemModel.Unbaked> fallback() {
            return fallback;
        }
    }
}
