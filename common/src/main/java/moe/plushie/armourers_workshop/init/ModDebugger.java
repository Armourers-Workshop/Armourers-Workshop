package moe.plushie.armourers_workshop.init;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ModDebugger {

    public static float rx = 0;
    public static float ry = 0;
    public static float rz = 0;
    public static float tx = 0;
    public static float ty = 0;
    public static float tz = 0;
    public static float sx = 1;
    public static float sy = 1;
    public static float sz = 1;

    public static boolean skinnableBlock = false;
    public static boolean hologramProjectorBlock = false;

    public static boolean skinBounds = false;
    public static boolean skinOrigin = false;

    public static boolean skinPartBounds = false;
    public static boolean skinPartOrigin = false;

    public static boolean targetBounds = false;

    public static boolean boundingBox = false;

    public static boolean mannequinCulling = false;

    public static boolean itemOverride = false;
    public static boolean handOverride = false;
    public static boolean modelOverride = false;

    public static boolean textureBounds = false;
    public static boolean spin = false;

    public static boolean tooltip = false;
    public static boolean wireframeRender = false;

    public static boolean viewHierarchy = false;

    // Debug tool
    public static boolean armourerDebugRender;
    public static boolean lodLevels;
    public static boolean skinBlockBounds;
    public static boolean skinRenderBounds;
    public static boolean sortOrderToolTip;

    @Environment(value = EnvType.CLIENT)
    public static void rotate(PoseStack matrixStack) {
        matrixStack.mulPose(new Quaternion(rx, ry, rz, true));
    }

    @Environment(value = EnvType.CLIENT)
    public static void scale(PoseStack matrixStack) {
        matrixStack.scale(sx, sy, sz);
    }

    @Environment(value = EnvType.CLIENT)
    public static void translate(PoseStack matrixStack) {
        matrixStack.translate(tx, ty, tz);
    }

    public static void init() {
    }
}
