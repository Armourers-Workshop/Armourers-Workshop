package moe.plushie.armourers_workshop.core.skin.animation;

import java.util.List;
import java.util.Map;

public class SkinAnimation {

    private final String name;

    private final SkinAnimationLoop loop;

    private final float duration;

    private final Map<String, List<SkinAnimationValue>> values;

    public SkinAnimation(String name, float duration, SkinAnimationLoop loop, Map<String, List<SkinAnimationValue>> values) {
        this.name = name;
        this.duration = duration;
        this.loop = loop;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public SkinAnimationLoop getLoop() {
        return loop;
    }

    public float getDuration() {
        return duration;
    }

    public Map<String, List<SkinAnimationValue>> getValues() {
        return values;
    }
}
