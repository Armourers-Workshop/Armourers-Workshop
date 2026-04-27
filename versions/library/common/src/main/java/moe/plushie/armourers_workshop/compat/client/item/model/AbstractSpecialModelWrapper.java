package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractSpecialModelRenderers;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public class AbstractSpecialModelWrapper implements AbstractItemModelImpl {

    public static class Unbaked implements AbstractItemModelImpl.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(OpenResourceKey.CODEC.fieldOf("base").forGetter(Unbaked::base), AbstractSpecialModelRenderers.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)).apply(instance, Unbaked::new));

        private final OpenResourceKey base;
        private final ISpecialModelRenderer<?> specialModel;

        public Unbaked(OpenResourceKey base, ISpecialModelRenderer<?> specialModel) {
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

        public ISpecialModelRenderer<?> specialModel() {
            return specialModel;
        }
    }
}

