package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.List;

public class SkinAnimationKeyframe {

    private final float time;

    private final String key;
    private final SkinAnimationFunction function;

    private final List<SkinAnimationPoint> points;

    public SkinAnimationKeyframe(float time, String key, SkinAnimationFunction function, List<SkinAnimationPoint> points) {
        this.time = time;
        this.key = key;
        this.function = function;
        this.points = points;
    }

    public float time() {
        return time;
    }

    public String key() {
        return key;
    }

    public SkinAnimationFunction function() {
        return function;
    }

    public List<SkinAnimationPoint> points() {
        return points;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "key", key, "time", time, "function", function);
    }
}
