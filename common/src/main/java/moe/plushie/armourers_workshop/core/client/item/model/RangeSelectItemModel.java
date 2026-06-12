package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

import java.util.Optional;


public class RangeSelectItemModel implements ItemModel {

    public static class Unbaked implements ItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(
                instance -> instance.group(
                                //RangeSelectItemModelProperties.CODEC.forGetter(it -> it.property),
                                IDataCodec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Unbaked::scale),
                                //RangeSelectItemModel.Entry.CODEC.listOf().fieldOf("entries").forGetter(it -> it.entries),
                                ItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)
                        )
                        .apply(instance, Unbaked::new)
        );

        //private final RangeSelectItemModelProperty property;
        private final float scale;
        //private final List<RangeSelectItemModel.Entry> entries;
        private final Optional<ItemModel.Unbaked> fallback;

        public Unbaked(float scale, Optional<ItemModel.Unbaked> fallback) {
            this.scale = scale;
            this.fallback = fallback;
        }

        @Override
        public IDataMapCodec<? extends Unbaked> type() {
            return MAP_CODEC;
        }

        public float scale() {
            return scale;
        }

        public Optional<ItemModel.Unbaked> fallback() {
            return fallback;
        }
    }
}
