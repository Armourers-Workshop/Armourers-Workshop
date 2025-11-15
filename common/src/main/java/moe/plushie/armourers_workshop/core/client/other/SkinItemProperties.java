package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinItemProperties {

    public static final SkinItemProperties EMPTY = new SkinItemProperties();

    private boolean allowOverrides = true;

    public void setAllowOverrides(boolean allowOverrides) {
        this.allowOverrides = allowOverrides;
    }

    public boolean isAllowOverrides() {
        return allowOverrides;
    }
}
