package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.client.IRenderType;

public class BakedRenderInfo {

    private boolean hasSolid = false;
    private boolean hasTranslucent = false;

    public void add(IRenderType renderType) {
        if (renderType.isTranslucent()) {
            hasTranslucent = true;
        } else {
            hasSolid = true;
        }
    }

    public boolean hasSolid() {
        return hasSolid;
    }

    public boolean hasTranslucent() {
        return hasTranslucent;
    }
}
