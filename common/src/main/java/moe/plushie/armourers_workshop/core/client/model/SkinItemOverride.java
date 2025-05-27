package moe.plushie.armourers_workshop.core.client.model;

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

    public SkinItemModel getModel() {
        return model;
    }

    public SkinItemProperty[] getProperties() {
        return properties;
    }

    public float[] getValues() {
        return values;
    }
}
