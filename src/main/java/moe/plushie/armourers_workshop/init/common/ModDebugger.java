package moe.plushie.armourers_workshop.init.common;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModDebugger {

    public static int rx = 0;
    public static int ry = 0;
    public static int rz = 0;
    public static int tx = 0;
    public static int ty = 0;
    public static int tz = 0;
    public static int sx = 1;
    public static int sy = 1;
    public static int sz = 1;

    public static boolean debugSkinnableBlock = false;
    public static boolean debugHologramProjectorBlock = false;

    public static boolean debugSkinBounds = false;
    public static boolean debugSkinOrigin = false;

    public static boolean debugSkinPartBounds = false;
    public static boolean debugSkinPartOrigin = false;

    public static boolean debugTargetBounds = false;

    public static boolean debugMannequinCulling = false;
    public static boolean debugItemOverride = false;

    public static boolean showDebugTextureBounds = false;
    public static boolean showDebugSpin = false;

    public static boolean debugTooltip = false;

    // Debug tool
    public static boolean showArmourerDebugRender;
    public static boolean showLodLevels;
    public static boolean showSkinBlockBounds;
    public static boolean showSkinRenderBounds;
    public static boolean showSortOrderToolTip;

    @OnlyIn(Dist.CLIENT)
    public static void rotate(MatrixStack matrixStack) {
        matrixStack.mulPose(new Quaternion(rx, ry, rz, true));
    }

    @OnlyIn(Dist.CLIENT)
    public static void scale(MatrixStack matrixStack) {
        matrixStack.scale(sx, sy, sz);
    }

    @OnlyIn(Dist.CLIENT)
    public static void translate(MatrixStack matrixStack) {
        matrixStack.translate(tx, ty, tz);
    }
}
