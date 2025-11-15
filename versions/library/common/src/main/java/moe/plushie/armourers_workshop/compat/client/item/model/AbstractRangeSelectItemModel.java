package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;

import java.util.Optional;


public class AbstractRangeSelectItemModel implements AbstractItemModel {

    public static class Unbaked implements AbstractItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(
                instance -> instance.group(
                                //RangeSelectItemModelProperties.CODEC.forGetter(it -> it.property),
                                IDataCodec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Unbaked::scale),
                                //RangeSelectItemModel.Entry.CODEC.listOf().fieldOf("entries").forGetter(it -> it.entries),
                                AbstractItemModels.CODEC.optionalFieldOf("fallback").forGetter(Unbaked::fallback)
                        )
                        .apply(instance, Unbaked::new)
        );

        //private final RangeSelectItemModelProperty property;
        private final float scale;
        //private final List<RangeSelectItemModel.Entry> entries;
        private final Optional<AbstractItemModel.Unbaked> fallback;

        public Unbaked(float scale, Optional<AbstractItemModel.Unbaked> fallback) {
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

        public Optional<AbstractItemModel.Unbaked> fallback() {
            return fallback;
        }
    }
}
