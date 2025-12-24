package moe.plushie.armourers_workshop.compat.client.block.model;

import moe.plushie.armourers_workshop.api.client.IBlockTintSource;
import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.compat.client.block.tintsource.AbstractBlockTintSources;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

import java.util.Collections;
import java.util.List;

public class AbstractBlockModel implements AbstractBlockModelImpl {

    public static class Unbaked implements AbstractBlockModelImpl.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(OpenResourceLocation.CODEC.fieldOf("model").forGetter(Unbaked::model), IDataCodec.STRING.optionalFieldOf("render_type", "default").forGetter(Unbaked::renderType), AbstractBlockTintSources.CODEC.listOf().optionalFieldOf("tints", Collections.emptyList()).forGetter(Unbaked::tints)).apply(instance, Unbaked::new));

        private final OpenResourceLocation model;
        private final String renderType;
        private final List<IBlockTintSource> tints;

        public Unbaked(OpenResourceLocation model, String renderType, List<IBlockTintSource> tints) {
            this.model = model;
            this.renderType = renderType;
            this.tints = tints;
        }

        @Override
        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public OpenResourceLocation model() {
            return model;
        }

        public String renderType() {
            return renderType;
        }

        public List<IBlockTintSource> tints() {
            return tints;
        }

        public boolean hasCustomTint() {
            // TODO: check the custom tint
            return !tints.isEmpty();
        }
    }
}
