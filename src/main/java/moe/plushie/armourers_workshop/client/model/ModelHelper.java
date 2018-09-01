package moe.plushie.armourers_workshop.client.model;

import org.lwjgl.opengl.GL11;

public final class ModelHelper {
    
    private static final float CHILD_SCALE = 2.0F;
    
    public static void enableChildModelScale(boolean headScale, float scale) {
        GL11.glPushMatrix();
        if (headScale) {
            GL11.glScalef(1.5F / CHILD_SCALE, 1.5F / CHILD_SCALE, 1.5F / CHILD_SCALE);
            GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
        } else {
            GL11.glScalef(1.0F / CHILD_SCALE, 1.0F / CHILD_SCALE, 1.0F / CHILD_SCALE);
            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
        }
    }
    
    public static void disableChildModelScale() {
        GL11.glPopMatrix();
    }
}
