package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.model.TransformModel;
import moe.plushie.armourers_workshop.core.render.layer.ForwardingLayer;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerSkinRenderer<T extends LivingEntity, M extends VillagerModel<T>> extends ExtendedSkinRenderer<T, M> {

    private final TransformModel<T> transformModel = new TransformModel<>(0.0f);

    public VillagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        super.initTransformers();
        // proxy => VillagerLevelPendantLayer
        // proxy => CrossedArmsItemLayer
        mappers.put(VillagerLevelPendantLayer.class, ForwardingLayer.when(this::isVisibleHat));
    }

    @Override
    public void willRender(T entity, M model, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        super.willRender(entity, model, light, partialRenderTick, matrixStack, buffers);
        transformModel.setup(entity, light, partialRenderTick);
    }

    @Override
    public void willRenderModel(T entity, M model, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        super.willRenderModel(entity, model, light, partialRenderTick, matrixStack, buffers);
        copyRot(transformModel.head, model.head);
    }

    @Override
    protected void applyOverriders(T entity, M model, SkinRenderData renderData) {
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
            addOverrider(model.arms);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addOverrider(model.arms);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
            addOverrider(model.head);
            addOverrider(model.hat); // when override the head, the hat needs to override too
            addOverrider(model.hatRim);
            addOverrider(model.nose);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
            addOverrider(model.body);
            addOverrider(model.jacket);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addOverrider(model.leg0);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addOverrider(model.leg1);
        }
    }

    private boolean isVisibleHat(T entity, M model) {
        return model.hat.visible;
    }

    private void copyRot(ModelRenderer model, ModelRenderer fromModel) {
        model.xRot = fromModel.xRot;
        model.yRot = fromModel.yRot;
        model.zRot = fromModel.zRot;
    }

    @Override
    public IPartAccessor<M> getAccessor() {
        return new IPartAccessor<M>() {

            public ModelRenderer getHat(M model) {
                return transformModel.hat;
            }

            public ModelRenderer getHead(M model) {
                return transformModel.head;
            }

            public ModelRenderer getBody(M model) {
                return transformModel.body;
            }

            public ModelRenderer getLeftArm(M model) {
                return transformModel.leftArm;
            }

            public ModelRenderer getRightArm(M model) {
                return transformModel.rightArm;
            }

            public ModelRenderer getLeftLeg(M model) {
                return transformModel.leftLeg;
            }

            public ModelRenderer getRightLeg(M model) {
                return transformModel.rightLeg;
            }
        };
    }
}
