package moe.plushie.armourers_workshop.core.skin.serializer;

import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.api.skin.ISkinDyeType;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SkinUsedCounter {

    private final Set<ISkinDyeType> dyeTypes = new HashSet<>();
    private final int[] cubeTotals = new int[SkinCubeTypes.getTotalCubes()];

    private int markerTotal;
    private int cubeTotal;
    private int faceTotal;

    public SkinUsedCounter() {
    }

    public void add(SkinUsedCounter counter) {
        markerTotal += counter.markerTotal;
        cubeTotal += counter.cubeTotal;
        for (int i = 0; i < cubeTotals.length; ++i) {
            cubeTotals[i] += counter.cubeTotals[i];
        }
    }

    public void addCube(int cubeId) {
        var cubeType = SkinCubeTypes.byId(cubeId);
        cubeTotal += 1;
        cubeTotals[cubeType.getId()] += 1;
    }

    public void addMarkers(int count) {
        markerTotal += count;
    }

    public void addPaints(Set<ISkinPaintType> paintTypes) {
        if (paintTypes == null) {
            return;
        }
        for (var paintType : paintTypes) {
            if (paintType.getDyeType() != null) {
                dyeTypes.add(paintType.getDyeType());
            }
        }
    }

    public void addFaceTotal(int total) {
        this.faceTotal += total;
    }

    public void reset() {
        dyeTypes.clear();
        markerTotal = 0;
        cubeTotal = 0;
        Arrays.fill(cubeTotals, 0);
    }

    public int getDyeTotal() {
        return dyeTypes.size();
    }

    public Set<ISkinDyeType> getDyeTypes() {
        return dyeTypes;
    }

    public int getMarkerTotal() {
        return markerTotal;
    }

    public int getCubeTotal(ISkinCubeType cube) {
        return cubeTotals[cube.getId()];
    }

    public int getCubeTotal() {
        return cubeTotal;
    }
}
