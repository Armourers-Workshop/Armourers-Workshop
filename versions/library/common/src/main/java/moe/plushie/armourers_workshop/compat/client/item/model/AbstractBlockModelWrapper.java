package moe.plushie.armourers_workshop.compat.client.item.model;

import moe.plushie.armourers_workshop.api.client.IItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.compat.client.item.tintsource.AbstractItemTintSources;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

import java.util.List;

public class AbstractBlockModelWrapper implements AbstractItemModelImpl {

    public static class Unbaked implements AbstractItemModelImpl.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(OpenResourceKey.CODEC.fieldOf("model").forGetter(Unbaked::model), AbstractItemTintSources.CODEC.listOf().optionalFieldOf("tints", Collections.emptyList()).forGetter(Unbaked::tints)).apply(instance, Unbaked::new));

        private final OpenResourceKey model;
        private final List<IItemTintSource> tints;

        public Unbaked(OpenResourceKey model, List<IItemTintSource> tints) {
            this.model = model;
            this.tints = tints;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public OpenResourceKey model() {
            return model;
        }

        public List<IItemTintSource> tints() {
            return tints;
        }

        public boolean hasCustomTint() {
            // TODO: check the custom tint
            return !tints.isEmpty();
        }
    }
}

