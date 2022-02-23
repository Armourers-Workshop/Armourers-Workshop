package moe.plushie.armourers_workshop.core.render.model;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import net.minecraft.client.renderer.entity.model.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MannequinModel<T extends MannequinEntity> extends PlayerModel<T> {

    public MannequinModel(float scale, boolean slim) {
        super(scale, slim);
    }

//    public void prepareMobModel(ArmorStandEntity p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
//        this.basePlate.xRot = 0.0F;
//        this.basePlate.yRot = ((float)Math.PI / 180F) * -MathHelper.rotLerp(p_212843_4_, p_212843_1_.yRotO, p_212843_1_.yRot);
//        this.basePlate.zRot = 0.0F;
//    }
//
//    public void setupAnim(ArmorStandEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
//        super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
//    }
//
//    public void translateToHand(HandSide p_225599_1_, MatrixStack p_225599_2_) {
//        ModelRenderer modelrenderer = this.getArm(p_225599_1_);
//        boolean flag = modelrenderer.visible;
//        modelrenderer.visible = true;
//        super.translateToHand(p_225599_1_, p_225599_2_);
//        modelrenderer.visible = flag;
//    }
}
