package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinCube;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;

import java.util.Arrays;
import java.util.Set;

public class SkinUsedCounter {

    private int markerTotal;
    private int dyeTotal;

    private int cubeTotal;
    private final int[] cubeTotals = new int[SkinCubes.getTotalCubes()];

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
        ISkinCube cube = SkinCubes.byId(cubeId);
        cubeTotal += 1;
        cubeTotals[cube.getId()] += 1;
    }

    public void addMarkers(int count) {
        markerTotal += count;
    }

    public void addPaints(Set<ISkinPaintType> paintTypes) {
        if (paintTypes == null) {
            return;
        }
        for (ISkinPaintType paintType : paintTypes) {
            if (paintType.getDyeType() != null) {
                dyeTotal += 1;
            }
        }
    }

    public void reset() {
        dyeTotal = 0;
        markerTotal = 0;
        cubeTotal = 0;
        Arrays.fill(cubeTotals, 0);
    }

    public int getDyeTotal() {
        return dyeTotal;
    }

    public int getMarkerTotal() {
        return markerTotal;
    }

    public int getCubeTotal(ISkinCube cube) {
        return cubeTotals[cube.getId()];
    }

    public int getCubeTotal() {
        return cubeTotal;
    }
}
