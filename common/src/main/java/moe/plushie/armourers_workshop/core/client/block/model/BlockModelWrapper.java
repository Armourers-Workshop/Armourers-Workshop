package moe.plushie.armourers_workshop.core.client.block.model;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSource;
import moe.plushie.armourers_workshop.core.client.block.tintsource.BlockTintSources;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

import java.util.Collections;
import java.util.List;

public class BlockModelWrapper implements BlockModel {

    public static class Unbaked implements BlockModel.Unbaked {

        public static final IDataMapCodec<Unbaked> MAP_CODEC = IDataMapCodec.create(instance -> instance.group(OpenResourceKey.CODEC.fieldOf("model").forGetter(Unbaked::model), IDataCodec.STRING.optionalFieldOf("render_type", "default").forGetter(Unbaked::renderType), BlockTintSources.CODEC.listOf().optionalFieldOf("tints", Collections.emptyList()).forGetter(Unbaked::tints)).apply(instance, Unbaked::new));

        private final OpenResourceKey model;
        private final String renderType;
        private final List<BlockTintSource> tints;

        public Unbaked(OpenResourceKey model, String renderType, List<BlockTintSource> tints) {
            this.model = model;
            this.renderType = renderType;
            this.tints = tints;
        }

        public IDataMapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        public OpenResourceKey model() {
            return model;
        }

        public String renderType() {
            return renderType;
        }

        public List<BlockTintSource> tints() {
            return tints;
        }

        public boolean hasCustomTint() {
            // TODO: check the custom tint
            return !tints.isEmpty();
        }
    }
}
