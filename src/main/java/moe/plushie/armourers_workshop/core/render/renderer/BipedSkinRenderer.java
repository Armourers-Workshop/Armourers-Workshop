package moe.plushie.armourers_workshop.core.render.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeState;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedSkinRenderer<T extends LivingEntity, M extends BipedModel<T>> extends ExtendedSkinRenderer<T, M> {

    public BipedSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void setHeadPart(MatrixStack matrixStack, M model) {
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

            public ModelRenderer getHat(M model) {
                return model.hat;
            }

            public ModelRenderer getHead(M model) {
                return model.head;
            }

            public ModelRenderer getBody(M model) {
                return model.body;
            }

            public ModelRenderer getLeftArm(M model) {
                return model.leftArm;
            }

            public ModelRenderer getRightArm(M model) {
                return model.rightArm;
            }

            public ModelRenderer getLeftLeg(M model) {
                return model.leftLeg;
            }

            public ModelRenderer getRightLeg(M model) {
                return model.rightLeg;
            }

        };
    }
}

