package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.model.TransformModel;
import moe.plushie.armourers_workshop.core.render.layer.ForwardingLayer;
import moe.plushie.armourers_workshop.core.render.other.SkinOverriddenManager;
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
    public void willRender(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        super.willRender(entity, model, renderData, light, partialRenderTick, matrixStack, buffers);
        transformModel.setup(entity, light, partialRenderTick);
    }

    @Override
    public void willRenderModel(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        super.willRenderModel(entity, model, renderData, light, partialRenderTick, matrixStack, buffers);
        copyRot(transformModel.head, model.head);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(model.arms);
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(model.arms);
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(model.head);
            addModelOverride(model.hat); // when override the head, the hat needs to override too
            addModelOverride(model.hatRim);
            addModelOverride(model.nose);
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_CHEST)) {
            addModelOverride(model.body);
            addModelOverride(model.jacket);
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_LEFT_LEG) || overriddenManager.hasModel(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addModelOverride(model.leg0);
        }
        if (overriddenManager.hasModel(SkinPartTypes.BIPED_RIGHT_LEG) || overriddenManager.hasModel(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addModelOverride(model.leg1);
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
