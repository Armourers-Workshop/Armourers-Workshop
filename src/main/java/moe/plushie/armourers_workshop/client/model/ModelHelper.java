package moe.plushie.armourers_workshop.client.model;

import net.minecraft.client.renderer.GlStateManager;

public final class ModelHelper {

    private static final float CHILD_SCALE = 2.0F;

    public static void enableChildModelScale(boolean headScale, float scale) {
        GlStateManager.pushMatrix();
        if (headScale) {
            GlStateManager.scale(1.5F / CHILD_SCALE, 1.5F / CHILD_SCALE, 1.5F / CHILD_SCALE);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
        } else {
            GlStateManager.scale(1.0F / CHILD_SCALE, 1.0F / CHILD_SCALE, 1.0F / CHILD_SCALE);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
        }
    }

    public static void disableChildModelScale() {
        GlStateManager.popMatrix();
    }
}
