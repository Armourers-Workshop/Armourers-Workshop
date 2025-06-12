package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.client.IRenderType;

public class BakedRenderInfo {

    private boolean hasSolid = false;
    private boolean hasTranslucent = false;
    private boolean hasEmissive = false;

    private int luminance = 0;

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

    public void setLuminance(int luminance) {
        this.luminance = luminance;
    }

    public int luminance() {
        return luminance;
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
}
