package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.model.FirstPersonPlayerModel;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

@Environment(value = EnvType.CLIENT)
public class FirstPersonSkinRenderer<T extends LivingEntity, V extends FirstPersonPlayerModel<T>, M extends IModelHolder<V>> extends SkinRenderer<T, V, M> {

    public FirstPersonSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_ARM, this::setLeftArm);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_ARM, this::setRightArm);
    }

    @Override
    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemStack itemStack, ItemTransforms.TransformType transformType) {
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

    public void setLeftArm(IPoseStack poseStack, T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        poseStack.translate(-5, -2, 0);
        poseStack.rotate(new OpenQuaternionf(180, 180, -5, true));
    }

    public void setRightArm(IPoseStack poseStack, T entity, M model, ItemStack itemStack, ItemTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        poseStack.translate(5, -2, 0);
        poseStack.rotate(new OpenQuaternionf(180, 180, 5, true));
    }
}
