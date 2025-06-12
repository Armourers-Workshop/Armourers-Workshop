package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.List;
import java.util.Map;

public class SkinAnimation {

    private final String name;

    private final SkinAnimationLoop loop;

    private final float duration;

    private final Map<String, List<SkinAnimationKeyframe>> keyframes;

    public SkinAnimation(String name, float duration, SkinAnimationLoop loop, Map<String, List<SkinAnimationKeyframe>> keyframes) {
        this.name = name;
        this.duration = duration;
        this.loop = loop;
        this.keyframes = keyframes;
    }

    public String name() {
        return name;
    }

    public SkinAnimationLoop loop() {
        return loop;
    }

    public float duration() {
        return duration;
    }

    public Map<String, List<SkinAnimationKeyframe>> keyframes() {
        return keyframes;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "duration", duration, "loop", loop, "keyframes", keyframes);
    }
}
