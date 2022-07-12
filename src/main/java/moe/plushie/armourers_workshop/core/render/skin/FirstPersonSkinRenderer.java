package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.model.FirstPersonPlayerModel;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FirstPersonSkinRenderer<T extends LivingEntity, M extends FirstPersonPlayerModel<T>> extends SkinRenderer<T, M> {

    public FirstPersonSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_ARM, this::setLeftArm);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_ARM, this::setRightArm);
    }

    @Override
    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
        switch (transformType) {
            case FIRST_PERSON_LEFT_HAND:
                if (bakedPart.getType() != SkinPartTypes.BIPED_LEFT_ARM) {
                    return false;
                }
                break;

            case FIRST_PERSON_RIGHT_HAND:
                if (bakedPart.getType() != SkinPartTypes.BIPED_RIGHT_ARM) {
                    return false;
                }
                break;

            default:
                break;
        }
        return super.prepare(entity, model, bakedSkin, bakedPart, itemStack, transformType);
    }

    public void setLeftArm(MatrixStack matrixStack, T entity, M model, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        matrixStack.translate(-5, -2, 0);
        matrixStack.mulPose(new Quaternion(180, 180, -5, true));
    }

    public void setRightArm(MatrixStack matrixStack, T entity, M model, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        matrixStack.translate(5, -2, 0);
        matrixStack.mulPose(new Quaternion(180, 180, 5, true));
    }
}
