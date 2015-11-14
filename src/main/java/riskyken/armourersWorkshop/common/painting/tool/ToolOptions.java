package riskyken.armourersWorkshop.common.painting.tool;

public class ToolOptions {
    
    private static final String TAG_FULL_BLOCK_MODE = "fullBlockMode";
    private static final String TAG_CHANGE_HUE = "changeHue";
    private static final String TAG_CHANGE_SATURATION = "changeSaturation";
    private static final String TAG_CHANGE_BRIGHTNESS = "changeBrightness";
    private static final String TAG_CHANGE_PAINT_TYPE = "changePaintType";
    
    public static final ToolOptionCheck FULL_BLOCK_MODE = new ToolOptionCheck(TAG_FULL_BLOCK_MODE);
    public static final ToolOptionCheck CHANGE_HUE = new ToolOptionCheck(TAG_CHANGE_HUE, false);
    public static final ToolOptionCheck CHANGE_SATURATION = new ToolOptionCheck(TAG_CHANGE_SATURATION, false);
    public static final ToolOptionCheck CHANGE_BRIGHTNESS = new ToolOptionCheck(TAG_CHANGE_BRIGHTNESS);
    public static final ToolOptionCheck CHANGE_PAINT_TYPE = new ToolOptionCheck(TAG_CHANGE_PAINT_TYPE);
    public static final ToolOptionIntensity INTENSITY = new ToolOptionIntensity();
    public static final ToolOptionRadius RADIUS = new ToolOptionRadius();
}
