package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.utils.TickUtils;

@SuppressWarnings("unused")
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

    public static float t = 1;

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
    public static boolean skinLocatorOrigin = false;

    public static boolean skinParticleOrigin = false;
    public static boolean skinParticleBounds = true;

    public static boolean targetBounds = false;

    public static boolean boundingBox = false;

    public static boolean mannequinCulling = false;

    public static boolean cubeItem = false;

    public static boolean itemOverride = false;
    public static boolean handOverride = false;
    public static boolean modelOverride = false;
    public static boolean fishingHook = false;
    public static boolean attachmentOverride = false;

    public static boolean textureBounds = false;
    public static boolean spin = false;

    public static boolean tooltip = false;
    public static boolean properties = false;

    public static boolean withoutVBO = false;
    public static boolean withoutAsyncVBO = false;

    public static boolean wireframeRender = false;

    public static boolean viewHierarchy = false;

    public static boolean armature = false;
    public static boolean defaultArmature = false;

    // Debug tool
    public static boolean armourerDebugRender;
    public static boolean lodLevels;
    public static boolean skinBlockBounds;
    public static boolean sortOrderToolTip;

    public static void rotate(IPoseStack poseStack) {
        poseStack.rotate(new OpenQuaternionf(rx, ry, rz, true));
    }

    public static void scale(IPoseStack poseStack) {
        poseStack.scale(sx, sy, sz);
    }

    public static void translate(IPoseStack poseStack) {
        poseStack.translate(tx, ty, tz);
    }

    public static void init() {
    }

    public static void apply() {
        TickUtils.setSpeed(animationSpeed);
    }
}
