package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class ModelMannequin extends ModelBiped {

    public ModelMannequin() {
        super();
        this.isChild = false;
    }
    
    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_,
            float p_78088_4_, float p_78088_5_, float p_78088_6_,
            float p_78088_7_) {
        Minecraft.getMinecraft().renderEngine.bindTexture(AbstractClientPlayer.locationStevePng);
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
        this.bipedHead.render(p_78088_7_);
        this.bipedBody.render(p_78088_7_);
        this.bipedRightArm.render(p_78088_7_);
        this.bipedLeftArm.render(p_78088_7_);
        this.bipedRightLeg.render(p_78088_7_);
        this.bipedLeftLeg.render(p_78088_7_);
        //this.bipedHeadwear.render(p_78088_7_);
        /*
        super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_,
                p_78088_6_, p_78088_7_);
        
        */
    }
    
}
