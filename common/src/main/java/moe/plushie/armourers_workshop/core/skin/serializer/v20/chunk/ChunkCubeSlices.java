package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.serializer.v20.coder.ChunkCubeCoders;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.SliceRandomlyAccessor;

import java.util.ArrayList;
import java.util.Collection;

public class ChunkCubeSlices extends SkinCubes {

    private int cubeTotal;

    private final SliceRandomlyAccessor<SkinCube> accessor;
    private final ArrayList<ChunkCubeSelector> selectors;
    private final ChunkPaletteData palette;

    public ChunkCubeSlices(ArrayList<ChunkCubeSelector> selectors, ChunkPaletteData palette) {
        this.palette = palette;
        this.selectors = selectors;
        this.accessor = new SliceRandomlyAccessor<>(ObjectUtils.map(selectors, this::build));
    }

    @Override
    public int getCubeTotal() {
        return cubeTotal;
    }

    @Override
    public SkinCube getCube(int index) {
        return accessor.get(index);
    }

    @Override
    public Collection<ISkinCubeType> getCubeTypes() {
        return ObjectUtils.map(selectors, it -> it.section.getCubeType());
    }

    public ChunkPaletteData getPalette() {
        return palette;
    }

    public Collection<ChunkCubeSelector> getSelectors() {
        return selectors;
    }

    private ChunkCubeSlice build(ChunkCubeSelector selector) {
        int startIndex = cubeTotal;
        cubeTotal += selector.count;
        return ChunkCubeCoders.createDecoder(startIndex, cubeTotal, selector, (ChunkCubeSection.Immutable) selector.section);
    }
}
