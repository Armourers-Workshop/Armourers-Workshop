package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeCoders;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SliceRandomlyAccessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChunkCubeSlices extends SkinCubes {

    private final int total;

    private final ThreadLocal<SliceRandomlyAccessor<SkinCube>> accessor;
    private final List<ChunkCubeSelector> selectors;
    private final ChunkPaletteData palette;

    public ChunkCubeSlices(int owner, List<ChunkCubeSelector> selectors, ChunkPaletteData palette) {
        this.owner = owner;
        this.palette = palette;
        this.selectors = selectors;
        this.accessor = ThreadLocal.withInitial(() -> build(selectors));
        this.total = ObjectUtils.sum(selectors, ChunkCubeSelector::getCount);
    }

    @Override
    public int getCubeTotal() {
        return total;
    }

    @Override
    public SkinCube getCube(int index) {
        return accessor.get().get(index);
    }

    @Override
    public Collection<ISkinCubeType> getCubeTypes() {
        return ObjectUtils.map(selectors, it -> it.getSection().getCubeType());
    }

    public ChunkPaletteData getPalette() {
        return palette;
    }

    public Collection<ChunkCubeSelector> getSelectors() {
        return selectors;
    }

    private static SliceRandomlyAccessor<SkinCube> build(List<ChunkCubeSelector> selectors) {
        var providers = new ArrayList<SliceRandomlyAccessor.Provider<? extends SkinCube>>();
        int startIndex = 0;
        int endIndex = 0;
        for (var selector : selectors) {
            endIndex += selector.getCount();
            providers.add(ChunkCubeCoders.createDecoder(startIndex, endIndex, selector, (ChunkCubeSection.Immutable) selector.getSection()));
            startIndex = endIndex;
        }
        return new SliceRandomlyAccessor<>(providers);
    }
}
