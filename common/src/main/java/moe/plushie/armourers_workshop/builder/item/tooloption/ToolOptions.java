package moe.plushie.armourers_workshop.builder.item.tooloption;

public class ToolOptions {

    public static final BooleanToolProperty FULL_BLOCK_MODE = new BooleanToolProperty("fullBlockMode", true);
    public static final BooleanToolProperty CHANGE_HUE = new BooleanToolProperty("changeHue", false);
    public static final BooleanToolProperty CHANGE_SATURATION = new BooleanToolProperty("changeSaturation", true);
    public static final BooleanToolProperty CHANGE_BRIGHTNESS = new BooleanToolProperty("changeBrightness", true);
    public static final BooleanToolProperty CHANGE_PAINT_TYPE = new BooleanToolProperty("changePaintType", true);

    public static final IntegerToolProperty INTENSITY = new IntegerToolProperty("intensity", 16, 1, 64);

    public static final IntegerToolProperty RADIUS = new IntegerToolProperty("radius", 3, 1, 6);
    public static final IntegerToolProperty RADIUS_SAMPLE = new IntegerToolProperty("sampleRadius", 2, 1, 6);
    public static final IntegerToolProperty RADIUS_EFFECT = new IntegerToolProperty("effectRadius", 1, 1, 6);

    public static final BooleanToolProperty PLANE_RESTRICT = new BooleanToolProperty("planeRestrict", true);
}
