package moe.plushie.armourers_workshop.core.skin.serializer;

import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinDyeType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SkinUsedCounter {

    private final Set<SkinDyeType> dyeTypes = new HashSet<>();
    private final int[] geometryTotals = new int[SkinGeometryTypes.getTotalCubes()];

    private int markerTotal;
    private int geometryTotal;
    private int faceTotal;

    public SkinUsedCounter() {
    }

    public void add(SkinUsedCounter counter) {
        markerTotal += counter.markerTotal;
        geometryTotal += counter.geometryTotal;
        for (int i = 0; i < geometryTotals.length; ++i) {
            geometryTotals[i] += counter.geometryTotals[i];
        }
    }

    public void addPaintType(Set<SkinPaintType> paintTypes) {
        if (paintTypes == null) {
            return;
        }
        for (var paintType : paintTypes) {
            if (paintType.getDyeType() != null) {
                dyeTypes.add(paintType.getDyeType());
            }
        }
    }

    public void addGeometryType(SkinGeometryType geometryType) {
        geometryTotal += 1;
        geometryTotals[geometryType.getId()] += 1;
    }

    public void addFaceTotal(int total) {
        this.faceTotal += total;
    }

    public void addMarkerTotal(int count) {
        markerTotal += count;
    }

    public void reset() {
        dyeTypes.clear();
        markerTotal = 0;
        geometryTotal = 0;
        Arrays.fill(geometryTotals, 0);
    }

    public SkinUsedCounter copy() {
        var result = new SkinUsedCounter();
        result.add(this);
        return result;
    }

    public int getDyeTotal() {
        return dyeTypes.size();
    }

    public Set<SkinDyeType> getDyeTypes() {
        return dyeTypes;
    }

    public int getMarkerTotal() {
        return markerTotal;
    }

    public int getGeometryTotal(SkinGeometryType geometryType) {
        return geometryTotals[geometryType.getId()];
    }

    public int getGeometryTotal() {
        return geometryTotal;
    }
}
