package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
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
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class VillagerSkinRenderer<T extends LivingEntity, V extends VillagerModel<T>, M extends IHumanoidModelHolder<V>> extends ExtendedSkinRenderer<T, V, M> {

    private final TransformModel<T> transformModel = new TransformModel<>(0.0f);
    private final M transformModelHolder = ObjectUtils.unsafeCast(ModelHolder.of(transformModel));

    public VillagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void willRender(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        super.willRender(entity, model, renderData, context);
        transformModel.setup(entity, context.light, context.partialTicks);
    }

    @Override
    public void willRenderModel(T entity, M model, SkinRenderData renderData, SkinRenderContext context) {
        super.willRenderModel(entity, model, renderData, context);
        copyRot(transformModelHolder.getHeadPart(), model.getHeadPart());
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

    private boolean isVisibleHat(T entity, M model) {
        ModelPart part = model.getHatPart();
        if (part != null) {
            return part.visible;
        }
        return false;
    }

    private void copyRot(ModelPart model, ModelPart fromModel) {
        model.xRot = fromModel.xRot;
        model.yRot = fromModel.yRot;
        model.zRot = fromModel.zRot;
    }

    @Override
    public M getOverrideModel(M model) {
        return transformModelHolder;
    }
}
