package moe.plushie.armourers_workshop.api.skin.geometry;

public interface ISkinGeometryOptions {

    // 0 default, 1 behind, 2 in_front
    int renderOrder();

    boolean isEmpty();

    long asLong();
}
