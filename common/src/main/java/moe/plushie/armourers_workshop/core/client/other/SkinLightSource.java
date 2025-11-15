package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinLightSource {

    private int luminance = 0;
    private boolean isEnabled = false;
    private boolean isWaterSensitive = false;

    public void update(int brightness) {
        this.luminance = brightness;
        this.isEnabled = brightness > 0;
    }

    public int get(int oldValue) {
        return Math.max(luminance, oldValue);
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
