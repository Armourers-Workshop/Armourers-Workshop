package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinModelTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class FirstPersonSkinRenderer<T extends LivingEntity, M extends IModel> extends SkinRenderer<T, M> {

    public FirstPersonSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void init(SkinModelTransformer<T, M> transformer) {
        transformer.registerArmor(SkinPartTypes.BIPPED_LEFT_ARM, this::setLeftArm);
        transformer.registerArmor(SkinPartTypes.BIPPED_RIGHT_ARM, this::setRightArm);
    }

    @Override
    public boolean prepare(T entity, M model, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinRenderContext context) {
        switch (context.getTransformType()) {
            case FIRST_PERSON_LEFT_HAND:
                if (bakedPart.getType() != SkinPartTypes.BIPPED_LEFT_ARM) {
                    return false;
                }
                break;

            case FIRST_PERSON_RIGHT_HAND:
                if (bakedPart.getType() != SkinPartTypes.BIPPED_RIGHT_ARM) {
                    return false;
                }
                break;

            default:
                break;
        }
        return super.prepare(entity, model, bakedPart, bakedSkin, context);
    }

    public void setLeftArm(IPoseStack poseStack, M model) {
        poseStack.translate(-5, -2, 0);
        poseStack.rotate(new OpenQuaternionf(180, 180, -5, true));
    }

    public void setRightArm(IPoseStack poseStack, M model) {
        poseStack.translate(5, -2, 0);
        poseStack.rotate(new OpenQuaternionf(180, 180, 5, true));
    }
}
