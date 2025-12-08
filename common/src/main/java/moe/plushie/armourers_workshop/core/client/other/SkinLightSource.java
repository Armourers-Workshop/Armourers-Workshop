package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinLightSource {

    private int brightness = 0;
    private boolean isEnabled = false;
    private boolean isWaterSensitive = false;

    public void clear() {
        this.brightness = 0;
        this.isEnabled = false;
    }

    public void add(int brightness) {
        this.brightness = brightness;
        this.isEnabled = brightness > 0;
    }

    public void add(SkinLightSource source) {
        add(source.get(brightness));
    }

    public void add(EntitySlot slot) {
        add(slot.lightSource());
    }

    public int get(int oldValue) {
        return Math.max(brightness, oldValue);
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
