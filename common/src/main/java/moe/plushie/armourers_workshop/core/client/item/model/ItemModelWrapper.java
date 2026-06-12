package moe.plushie.armourers_workshop.core.client.item.model;

import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSource;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.client.item.tintsource.ItemTintSources;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

import java.util.List;

public class ItemModelWrapper implements ItemModel {

    public static class Unbaked implements ItemModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(OpenResourceKey.CODEC.fieldOf("model").forGetter(Unbaked::model), ItemTintSources.CODEC.listOf().optionalFieldOf("tints", Collections.emptyList()).forGetter(Unbaked::tints)).apply(instance, Unbaked::new));

        private final OpenResourceKey model;
        private final List<ItemTintSource> tints;

        public Unbaked(OpenResourceKey model, List<ItemTintSource> tints) {
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

        public List<ItemTintSource> tints() {
            return tints;
        }

        public boolean hasCustomTint() {
            // TODO: check the custom tint
            return !tints.isEmpty();
        }
    }
}

