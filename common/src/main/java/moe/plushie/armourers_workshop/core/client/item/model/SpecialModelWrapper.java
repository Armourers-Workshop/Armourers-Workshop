package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderers;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class SpecialModelWrapper implements ItemModel {

    public static class Unbaked implements ItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(OpenResourceKey.CODEC.fieldOf("base").forGetter(Unbaked::base), SpecialModelRenderers.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)).apply(instance, Unbaked::new));

        private final OpenResourceKey base;
        private final SpecialModelRenderer<?> specialModel;

        public Unbaked(OpenResourceKey base, SpecialModelRenderer<?> specialModel) {
            this.base = base;
            this.specialModel = specialModel;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public OpenResourceKey base() {
            return base;
        }

        public SpecialModelRenderer<?> specialModel() {
            return specialModel;
        }
    }
}

