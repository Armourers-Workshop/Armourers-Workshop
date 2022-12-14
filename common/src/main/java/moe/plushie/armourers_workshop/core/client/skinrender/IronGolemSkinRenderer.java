package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.ItemStack;

@Environment(value = EnvType.CLIENT)
public class IronGolemSkinRenderer<T extends IronGolem, V extends IronGolemModel<T>, M extends IHumanoidModelHolder<V>> extends ExtendedSkinRenderer<T, V, M> {

    public IronGolemSkinRenderer(EntityProfile profile) {
        super(profile);
    }

//    @Override
//    protected void setHeadPart(IPoseStack poseStack, M model) {
//        super.setHeadPart(poseStack, model);
//        poseStack.translate(0.0f, -2.0f, -1.5f);
//    }
//
//    @Override
//    protected void setBodyPart(IPoseStack poseStack, M model) {
//        poseStack.translate(0, -2, 0);
//        super.setBodyPart(poseStack, model);
//    }
//
//    @Override
//    protected void setLeftArmPart(IPoseStack poseStack, M model) {
//        poseStack.translate(10, 0, 0);
//        super.setLeftArmPart(poseStack, model);
//    }
//
//    @Override
//    protected void setRightArmPart(IPoseStack poseStack, M model) {
//        poseStack.translate(-10, 0, 0);
//        super.setRightArmPart(poseStack, model);
//    }
//
//    @Override
//    protected void setLeftLegPart(IPoseStack poseStack, M model) {
//        super.setLeftLegPart(poseStack, model);
//        poseStack.translate(-0.5f, -3.0f, -1.0f);
//    }
//
//    @Override
//    protected void setRightLegPart(IPoseStack poseStack, M model) {
//        super.setRightLegPart(poseStack, model);
//        poseStack.translate(-0.5f, -3.0f, -1.0f);
//    }
//
//    @Override
//    protected void setLeftFootPart(IPoseStack poseStack, M model) {
//        super.setLeftFootPart(poseStack, model);
//        poseStack.translate(-0.5f, 1.0f, -1.0f);
//    }
//
//    @Override
//    protected void setRightFootPart(IPoseStack poseStack, M model) {
//        super.setRightFootPart(poseStack, model);
//        poseStack.translate(-0.5f, 1.0f, -1.0f);
//    }
//
//    @Override
//    protected void setSkirtPart(IPoseStack poseStack, M model) {
//        super.setSkirtPart(poseStack, model);
//        poseStack.translate(-0.5f, -3.0f, -1.0f);
//    }
//
//    @Override
//    protected void setWings(IPoseStack poseStack, T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart) {
//        poseStack.translate(0, -2, 3);
//        super.setWings(poseStack, entity, model, itemStack, transformType, bakedPart);
//    }
}
