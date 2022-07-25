package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class BipedSkinRenderer<T extends LivingEntity, M extends HumanoidModel<T>> extends ExtendedSkinRenderer<T, M> {

    public BipedSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void setHeadPart(PoseStack matrixStack, M model) {
        super.setHeadPart(matrixStack, model);
        if (model.young) {
            float scale = model.babyBodyScale;
            if (model.scaleHead) {
                scale = 1.5f;
            }
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(0, model.yHeadOffset / 16.0f, model.zHeadOffset / 16.0f);
        }
    }

    @Override
    public IPartAccessor<M> getAccessor() {
        return new IPartAccessor<M>() {

            public ModelPart getHat(M model) {
                return model.hat;
            }

            public ModelPart getHead(M model) {
                return model.head;
            }

            public ModelPart getBody(M model) {
                return model.body;
            }

            public ModelPart getLeftArm(M model) {
                return model.leftArm;
            }

            public ModelPart getRightArm(M model) {
                return model.rightArm;
            }

            public ModelPart getLeftLeg(M model) {
                return model.leftLeg;
            }

            public ModelPart getRightLeg(M model) {
                return model.rightLeg;
            }

        };
    }
}

