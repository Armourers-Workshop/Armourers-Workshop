package moe.plushie.armourers_workshop.core.skin.cube;

import moe.plushie.armourers_workshop.core.api.ISkinCube;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;

import java.util.Arrays;
import java.util.Set;

public class SkinUsedCounter {

    private int markerTotal;
    private int dyeTotal;

    private int cubeTotal;
    private final int[] cubeTotals = new int[SkinCubes.getTotalCubes()];

    public SkinUsedCounter() {
    }

    public void add(SkinUsedCounter tracker) {
        markerTotal += tracker.markerTotal;
        cubeTotal += tracker.cubeTotal;;
        for (int i = 0; i < cubeTotals.length; ++i) {
            cubeTotals[i] += tracker.cubeTotals[i];
        }
    }

    public void addCube(int cubeId) {
        cubeTotal += 1;
        cubeTotals[cubeId] += 1;
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
