package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
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

    @Override
    protected void setHeadPart(PoseStack matrixStack, M model) {
        super.setHeadPart(matrixStack, model);
        matrixStack.translate(0.0f, -2.0f, -1.5f);
    }

    @Override
    protected void setBodyPart(PoseStack matrixStack, M model) {
        matrixStack.translate(0, -2, 0);
        super.setBodyPart(matrixStack, model);
    }

    @Override
    protected void setLeftArmPart(PoseStack matrixStack, M model) {
        matrixStack.translate(10, 0, 0);
        super.setLeftArmPart(matrixStack, model);
    }

    @Override
    protected void setRightArmPart(PoseStack matrixStack, M model) {
        matrixStack.translate(-10, 0, 0);
        super.setRightArmPart(matrixStack, model);
    }

    @Override
    protected void setLeftLegPart(PoseStack matrixStack, M model) {
        super.setLeftLegPart(matrixStack, model);
        matrixStack.translate(-0.5f, -3.0f, -1.0f);
    }

    @Override
    protected void setRightLegPart(PoseStack matrixStack, M model) {
        super.setRightLegPart(matrixStack, model);
        matrixStack.translate(-0.5f, -3.0f, -1.0f);
    }

    @Override
    protected void setLeftFootPart(PoseStack matrixStack, M model) {
        super.setLeftFootPart(matrixStack, model);
        matrixStack.translate(-0.5f, 1.0f, -1.0f);
    }

    @Override
    protected void setRightFootPart(PoseStack matrixStack, M model) {
        super.setRightFootPart(matrixStack, model);
        matrixStack.translate(-0.5f, 1.0f, -1.0f);
    }

    @Override
    protected void setSkirtPart(PoseStack matrixStack, M model) {
        super.setSkirtPart(matrixStack, model);
        matrixStack.translate(-0.5f, -3.0f, -1.0f);
    }

    @Override
    protected void setWings(PoseStack matrixStack, T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        matrixStack.translate(0, -2, 3);
        super.setWings(matrixStack, entity, model, itemStack, transformType, bakedPart);
    }
}
