package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinLightSource;

public class BakedRenderInfo {

    private boolean hasSolid = false;
    private boolean hasTranslucent = false;
    private boolean hasEmissive = false;

    private final SkinLightSource lightSource = new SkinLightSource();

    public void add(IRenderType renderType) {
        if (renderType.isTranslucent()) {
            hasTranslucent = true;
        } else {
            hasSolid = true;
        }
        if (renderType.isEmissive()) {
            hasEmissive = true;
        }
    }

    public boolean hasSolid() {
        return hasSolid;
    }

    public boolean hasTranslucent() {
        return hasTranslucent;
    }

    public boolean hasEmissive() {
        return hasEmissive;
    }

    public SkinLightSource lightSource() {
        return lightSource;
    }
}
