package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.client.model.TransformModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinVisibilityTransformer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class VillagerSkinRenderer<T extends LivingEntity, M extends IHumanoidModel> extends ExtendedSkinRenderer<T, M> {

    private final TransformModel<T> transformModelRef = new TransformModel<>(0.0f);
    private final M transformModel = ObjectUtils.unsafeCast(ModelHolder.of(transformModelRef));

    public VillagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void init(SkinVisibilityTransformer<M> transformer) {

        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, M::getLeftArmPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, M::getRightArmPart);

        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_HEAD, M::getHatPart); // when override the head, the hat needs to override too
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_HEAD, M::getHeadPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_HEAD, "hat_rim");
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_HEAD, "nose");

        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_CHEST, M::getBodyPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_CHEST, "jacket");

        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, M::getLeftLegPart);
        transformer.linkToPart(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, M::getRightLegPart);
    }

    @Override
    public void willRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        super.willRender(entity, model, renderData, context);
        transformModelRef.setup(entity, context.getLightmap(), context.getPartialTicks());
    }

    @Override
    public void willRenderModel(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        super.willRenderModel(entity, model, renderData, context);
        copyRot(transformModel.getHeadPart(), model.getHeadPart());
    }

    private void copyRot(IModelPart model, IModelPart fromModel) {
        IModelPartPose pose1 = model.pose();
        IModelPartPose pose2 = fromModel.pose();
        pose1.setXRot(pose2.getXRot());
        pose1.setYRot(pose2.getYRot());
        pose1.setZRot(pose2.getZRot());
    }

    @Override
    public M getOverrideModel(M model) {
        return transformModel;
    }
}
