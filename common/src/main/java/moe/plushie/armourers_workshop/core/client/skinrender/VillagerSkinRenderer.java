package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.core.client.model.TransformModel;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
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
    public void willRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        super.willRender(entity, model, renderData, context);
        transformModelRef.setup(entity, context.getLightmap(), context.getPartialTicks());
    }

    @Override
    public void willRenderModel(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        super.willRenderModel(entity, model, renderData, context);
        copyRot(transformModel.getHeadPart(), model.getHeadPart());
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_LEFT_ARM)) {
            addModelOverride(model.getLeftArmPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_RIGHT_ARM)) {
            addModelOverride(model.getRightArmPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_HEAD)) {
            addModelOverride(model.getHeadPart());
            addModelOverride(model.getHatPart()); // when override the head, the hat needs to override too
            addModelOverride(model.getPart("hat_rim"));
            addModelOverride(model.getPart("nose"));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_CHEST)) {
            addModelOverride(model.getBodyPart());
            addModelOverride(model.getPart("jacket"));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_LEFT_LEG) || overriddenManager.overrideModel(SkinPartTypes.BIPPED_LEFT_FOOT)) {
            addModelOverride(model.getLeftLegPart());
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_RIGHT_LEG) || overriddenManager.overrideModel(SkinPartTypes.BIPPED_RIGHT_FOOT)) {
            addModelOverride(model.getRightLegPart());
        }
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
