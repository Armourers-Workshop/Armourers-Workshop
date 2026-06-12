package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

import java.util.Optional;

public class SelectItemModel implements ItemModel {

    public static class Unbaked implements ItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(
                instance -> instance.group(
                                //SelectItemModel.UnbakedSwitch.CODEC.forGetter(it -> it.unbakedSwitch),
                                ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)
                        )
                        .apply(instance, Unbaked::new)
        );

        //private final SelectItemModel.UnbakedSwitch<?, ?> unbakedSwitch;
        private final Optional<ItemModel.Unbaked> fallback;

        public Unbaked(Optional<ItemModel.Unbaked> fallback) {
            this.fallback = fallback;
        }

        @Override
        public IDataMapCodec<? extends Unbaked> type() {
            return MAP_CODEC;
        }

        public Optional<ItemModel.Unbaked> fallback() {
            return fallback;
        }
    }
}
