package moe.plushie.armourers_workshop.init;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
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

    public static int flag0 = 0;
    public static int flag1 = 0;
    public static int flag2 = 0;
    public static int flag3 = 0;

    public static float animationSpeed = 1f;

    public static boolean skinnable = false;
    public static boolean hologramProjector = false;
    public static boolean advancedBuilder = false;

    public static boolean skinBounds = false;
    public static boolean skinOrigin = false;

    public static boolean skinPartBounds = false;
    public static boolean skinPartOrigin = false;
    public static boolean skinPartCombiner = false;

    public static boolean targetBounds = false;

    public static boolean boundingBox = false;

    public static boolean mannequinCulling = false;

    public static boolean itemOverride = false;
    public static boolean handOverride = false;
    public static boolean modelOverride = false;
    public static boolean fishingHook = false;

    public static boolean textureBounds = false;
    public static boolean spin = false;

    public static boolean tooltip = false;
    public static boolean properties = false;

    public static boolean vbo = false;
    public static boolean wireframeRender = false;

    public static boolean viewHierarchy = false;

    public static boolean armature = false;
    public static boolean defaultArmature = false;

    // Debug tool
    public static boolean armourerDebugRender;
    public static boolean lodLevels;
    public static boolean skinBlockBounds;
    public static boolean skinRenderBounds;
    public static boolean sortOrderToolTip;

    public static void rotate(IPoseStack poseStack) {
        poseStack.rotate(new OpenQuaternionf(rx, ry, rz, true));
    }

    @Environment(EnvType.CLIENT)
    public static void rotate(PoseStack poseStack) {
        poseStack.mulPose(new OpenQuaternionf(rx, ry, rz, true));
    }

    public static void scale(IPoseStack poseStack) {
        poseStack.scale(sx, sy, sz);
    }

    @Environment(EnvType.CLIENT)
    public static void scale(PoseStack poseStack) {
        poseStack.scale(sx, sy, sz);
    }

    public static void translate(IPoseStack poseStack) {
        poseStack.translate(tx, ty, tz);
    }

    @Environment(EnvType.CLIENT)
    public static void translate(PoseStack poseStack) {
        poseStack.translate(tx, ty, tz);
    }

    public static void init() {
    }
}
