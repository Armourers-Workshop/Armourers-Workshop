package riskyken.armourersWorkshop.common.painting.tool;

public class ToolOptions {
    
    private static final String TAG_FULL_BLOCK_MODE = "fullBlockMode";
    private static final String TAG_CHANGE_HUE = "changeHue";
    private static final String TAG_CHANGE_SATURATION = "changeSaturation";
    private static final String TAG_CHANGE_BRIGHTNESS = "changeBrightness";
    private static final String TAG_CHANGE_PAINT_TYPE = "changePaintType";
    
    public static final AbstractToolOption FULL_BLOCK_MODE = new ToolOptionCheck(TAG_FULL_BLOCK_MODE);
    public static final AbstractToolOption CHANGE_HUE = new ToolOptionCheck(TAG_CHANGE_HUE, false);
    public static final AbstractToolOption CHANGE_SATURATION = new ToolOptionCheck(TAG_CHANGE_SATURATION, false);
    public static final AbstractToolOption CHANGE_BRIGHTNESS = new ToolOptionCheck(TAG_CHANGE_BRIGHTNESS);
    public static final AbstractToolOption CHANGE_PAINT_TYPE = new ToolOptionCheck(TAG_CHANGE_PAINT_TYPE);
    public static final AbstractToolOption INTENSITY = new ToolOptionIntensity();
    public static final AbstractToolOption RADIUS = new ToolOptionRadius();
}
