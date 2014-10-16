package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class ModelMannequin extends ModelBiped {

    public ModelMannequin() {
        super();
        this.isChild = false;
    }
    
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_,
            float p_78088_4_, float p_78088_5_, float p_78088_6_,
            float p_78088_7_, boolean headOverlay) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
        GL11.glColor3f(1F, 1F, 1F);
        this.bipedHead.render(p_78088_7_);
        this.bipedBody.render(p_78088_7_);
        this.bipedRightArm.render(p_78088_7_);
        this.bipedLeftArm.render(p_78088_7_);
        this.bipedRightLeg.render(p_78088_7_);
        this.bipedLeftLeg.render(p_78088_7_);
        if (headOverlay) {
            this.bipedHeadwear.render(p_78088_7_);
        }
        GL11.glColor3f(1F, 1F, 1F);
    }
}
