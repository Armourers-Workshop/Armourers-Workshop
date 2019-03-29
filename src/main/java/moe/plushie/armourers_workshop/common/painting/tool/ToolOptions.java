package moe.plushie.armourers_workshop.common.painting.tool;

public class ToolOptions {
    
    private static final String TAG_FULL_BLOCK_MODE = "fullBlockMode";
    private static final String TAG_CHANGE_HUE = "changeHue";
    private static final String TAG_CHANGE_SATURATION = "changeSaturation";
    private static final String TAG_CHANGE_BRIGHTNESS = "changeBrightness";
    private static final String TAG_CHANGE_PAINT_TYPE = "changePaintType";
    private static final String TAG_INTENSITY = "intensity";
    private static final String TAG_RADIUS = "radius";
    private static final String TAG_RADIUS_SAMPLE = TAG_RADIUS + ".sample";
    private static final String TAG_RADIUS_EFFECT = TAG_RADIUS + ".effect";
    private static final String TAG_PLANE_RESTRICT = "planeRestrict";
    
    public static final ToolOptionCheck FULL_BLOCK_MODE = new ToolOptionCheck(TAG_FULL_BLOCK_MODE, true);
    public static final ToolOptionCheck CHANGE_HUE = new ToolOptionCheck(TAG_CHANGE_HUE, false);
    public static final ToolOptionCheck CHANGE_SATURATION = new ToolOptionCheck(TAG_CHANGE_SATURATION, true);
    public static final ToolOptionCheck CHANGE_BRIGHTNESS = new ToolOptionCheck(TAG_CHANGE_BRIGHTNESS, true);
    public static final ToolOptionCheck CHANGE_PAINT_TYPE = new ToolOptionCheck(TAG_CHANGE_PAINT_TYPE, true);
    public static final ToolOptionIntensity INTENSITY = new ToolOptionIntensity(TAG_INTENSITY, 16);
    public static final ToolOptionRadius RADIUS = new ToolOptionRadius(TAG_RADIUS, 3);
    public static final ToolOptionRadius RADIUS_SAMPLE = new ToolOptionRadius(TAG_RADIUS_SAMPLE, 2);
    public static final ToolOptionRadius RADIUS_EFFECT = new ToolOptionRadius(TAG_RADIUS_EFFECT, 1);
    public static final ToolOptionCheck PLANE_RESTRICT = new ToolOptionCheck(TAG_PLANE_RESTRICT, true);
}
