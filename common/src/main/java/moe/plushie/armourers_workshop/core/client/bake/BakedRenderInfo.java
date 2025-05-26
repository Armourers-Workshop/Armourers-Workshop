package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import net.minecraft.client.renderer.RenderType;

public class BakedRenderInfo {

    private boolean hasSoild = false;
    private boolean hasGlowing = false;
    private boolean hasTranslucent = false;

    public void add(RenderType renderType) {
        // ii
        if (SkinRenderType.isTranslucent(renderType)) {
            hasTranslucent = true;
        } else {
            hasSoild = true;
        }
        if (!hasGlowing && SkinRenderType.isGrowing(renderType)) {
            hasGlowing = true;
        }
    }

    public boolean hasGlowing() {
        return hasGlowing;
    }

    public boolean hasSolid() {
        return hasSoild;
    }

    public boolean hasTranslucent() {
        return hasTranslucent;
    }

}
