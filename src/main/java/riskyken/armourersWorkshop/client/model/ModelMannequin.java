package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.BipedRotations;

public class ModelMannequin extends ModelBiped {

    public ModelMannequin() {
        super();
        this.isChild = false;
    }
    
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_,
            float p_78088_4_, float p_78088_5_, float p_78088_6_,
            float scale, boolean headOverlay) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, p_78088_1_);
        GL11.glColor3f(1F, 1F, 1F);
        this.bipedHead.render(scale);
        this.bipedBody.render(scale);
        this.bipedRightArm.render(scale);
        this.bipedLeftArm.render(scale);
        this.bipedRightLeg.render(scale);
        this.bipedLeftLeg.render(scale);
        if (headOverlay) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            this.bipedHeadwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    public void render(BipedRotations bipedRotations,  boolean headOverlay, float scale) {
        GL11.glColor3f(1F, 1F, 1F);
        bipedRotations.applyRotationsToBiped(this);
        this.bipedHead.render(scale);
        this.bipedBody.render(scale);
        this.bipedRightArm.render(scale);
        this.bipedLeftArm.render(scale);
        this.bipedRightLeg.render(scale);
        this.bipedLeftLeg.render(scale);
        if (headOverlay) {
            GL11.glDisable(GL11.GL_CULL_FACE);
            this.bipedHeadwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        GL11.glColor3f(1F, 1F, 1F);
    }
}
