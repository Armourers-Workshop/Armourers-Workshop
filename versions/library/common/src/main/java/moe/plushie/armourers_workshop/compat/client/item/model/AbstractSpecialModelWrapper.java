package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.compat.client.renderer.special.AbstractSpecialModelRenderers;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

public class AbstractSpecialModelWrapper implements AbstractItemModelImpl {

    public static class Unbaked implements AbstractItemModelImpl.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(OpenResourceLocation.CODEC.fieldOf("base").forGetter(Unbaked::base), AbstractSpecialModelRenderers.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)).apply(instance, Unbaked::new));

        private final OpenResourceLocation base;
        private final ISpecialModelRenderer<?> specialModel;

        public Unbaked(OpenResourceLocation base, ISpecialModelRenderer<?> specialModel) {
            this.base = base;
            this.specialModel = specialModel;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public OpenResourceLocation base() {
            return base;
        }

        public ISpecialModelRenderer<?> specialModel() {
            return specialModel;
        }
    }
}

