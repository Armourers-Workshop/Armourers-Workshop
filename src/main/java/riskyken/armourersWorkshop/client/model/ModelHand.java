package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

public class ModelHand extends ModelBase {
    private ModelRenderer arm1;
    
    public ModelHand() {
        arm1 = new ModelRenderer(this, 40, 16);
        arm1.addBox(0, 0, 0, 4, 12, 4);
        arm1.setRotationPoint(0, 0, 0);
    }
    
    public void render() {
        float mult = 0.0625F;
        GL11.glPushMatrix();
        GL11.glTranslatef(mult * -2F, mult * -23F, mult * 10F);
        GL11.glRotatef(-90, 1, 0, 0);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        arm1.render(mult);
        GL11.glPopMatrix();
    }
}
