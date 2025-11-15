package moe.plushie.armourers_workshop.core.client.render.model;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinItemOverride {

    private SkinItemModel model;

    private final SkinItemProperty[] properties;
    private final float[] values;

    public SkinItemOverride(SkinItemProperty[] properties, float[] values) {
        this.properties = properties;
        this.values = values;
    }

    public void setModel(SkinItemModel model) {
        this.model = model;
    }

    public SkinItemModel model() {
        return model;
    }

    public SkinItemProperty[] properties() {
        return properties;
    }

    public float[] values() {
        return values;
    }
}
