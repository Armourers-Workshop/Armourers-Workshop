package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.layer.ForwardingLayer;
import moe.plushie.armourers_workshop.core.client.model.TransformModel;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class VillagerSkinRenderer<T extends LivingEntity, M extends VillagerModel<T>> extends ExtendedSkinRenderer<T, M> {

    private final TransformModel<T> transformModel = new TransformModel<>(0.0f);

    public VillagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        super.initTransformers();
        // proxy => VillagerProfessionLayer
        // proxy => CrossedArmsItemLayer
        mappers.put(VillagerProfessionLayer.class, ForwardingLayer.when(this::isVisibleHat));
    }

    @Override
    public void willRender(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, PoseStack matrixStack, MultiBufferSource buffers) {
        super.willRender(entity, model, renderData, light, partialRenderTick, matrixStack, buffers);
        transformModel.setup(entity, light, partialRenderTick);
    }

    @Override
    public void willRenderModel(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, PoseStack matrixStack, MultiBufferSource buffers) {
        super.willRenderModel(entity, model, renderData, light, partialRenderTick, matrixStack, buffers);
        copyRot(transformModel.head, model.head);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(model.arms);
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(model.arms);
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(model.head);
            addModelOverride(model.hat); // when override the head, the hat needs to override too
            addModelOverride(model.hatRim);
            addModelOverride(model.nose);
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_CHEST)) {
            addModelOverride(model.body);
            addModelOverride(model.jacket);
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_LEG) || overriddenManager.overrideModel(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addModelOverride(model.leg0);
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_LEG) || overriddenManager.overrideModel(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addModelOverride(model.leg1);
        }
    }

    private boolean isVisibleHat(T entity, M model) {
        return model.hat.visible;
    }

    private void copyRot(ModelPart model, ModelPart fromModel) {
        model.xRot = fromModel.xRot;
        model.yRot = fromModel.yRot;
        model.zRot = fromModel.zRot;
    }

    @Override
    public IPartAccessor<M> getAccessor() {
        return new IPartAccessor<M>() {

            public ModelPart getHat(M model) {
                return transformModel.hat;
            }

            public ModelPart getHead(M model) {
                return transformModel.head;
            }

            public ModelPart getBody(M model) {
                return transformModel.body;
            }

            public ModelPart getLeftArm(M model) {
                return transformModel.leftArm;
            }

            public ModelPart getRightArm(M model) {
                return transformModel.rightArm;
            }

            public ModelPart getLeftLeg(M model) {
                return transformModel.leftLeg;
            }

            public ModelPart getRightLeg(M model) {
                return transformModel.rightLeg;
            }
        };
    }
}
