package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformer;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightTransformProvider;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager2;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;

public class EpicFlightRenderContext {

    public boolean isLimitLimbs = false;

    //public Collection<ISkinPartType> overrideParts;

    private boolean isActivate = false;
    private boolean isFirstPerson = false;

    private PoseStack poseStack;
    private final EpicFlightModel model;
    private final SkinRenderContext context;
    private final BakedArmatureTransformer transformer;

    public EpicFlightRenderContext(EpicFlightModel model, SkinRenderContext context) {
        this.model = model;
        this.transformer = model.getTransformer();
        this.poseStack = context.pose().pose();
        this.context = context;
    }

    public static EpicFlightRenderContext of(Entity entity) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            return renderData.epicFlightContext;
        }
        return null;
    }

    public static EpicFlightRenderContext alloc(Entity entity, LivingEntityRenderer<?, ?> entityRenderer, int light, float partialTick, PoseStack poseStack, MultiBufferSource buffers) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        EpicFlightModel model = EpicFlightModel.ofNullable(entityRenderer.getModel());
        if (renderData == null || model == null || model.isInvalid()) {
            return null;
        }
        if (model.getTransformer() == null) {
            ArmatureTransformer transformer = SkinRendererManager2.EPICFIGHT.getTransformer(entity.getType(), model);
            model.setTransformer(BakedArmatureTransformer.create(transformer, entityRenderer));
        }
        if (model.getTransformer() == null) {
            model.setInvalid(true);
            return null;
        }
        SkinRenderContext context = SkinRenderContext.alloc(renderData, light, partialTick, AbstractItemTransformType.NONE, poseStack, buffers);
        EpicFlightRenderContext context1 = new EpicFlightRenderContext(model, context);
        renderData.epicFlightContext = context1;
        return context1;
    }

    public static void release(Entity entity) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            EpicFlightRenderContext context = renderData.epicFlightContext;
            context.transformer.setFilter(null);
            context.context.release();
            renderData.epicFlightContext = null;
        }
    }

    public void prepare(Entity entity) {
        transformer.prepare(entity, context);
    }

    public void activate(Entity entity) {
        transformer.activate(entity, context);
        isActivate = true;
    }

    public void deactivate(Entity entity) {
        if (isActivate) {
            transformer.deactivate(entity, context);
            isActivate = false;
        }
    }

    public void setMesh(Object mesh) {
        model.linkTo(mesh);
    }

    public void setTransformProvider(EpicFlightTransformProvider transformProvider) {
        model.setAssociatedObject(transformProvider, EpicFlightTransformProvider.KEY);
    }


    public void setFirstPerson(boolean isFirstPerson) {
        this.isFirstPerson = isFirstPerson;
        if (isFirstPerson) {
            this.transformer.setFilter(joint -> !joint.getName().equals("Head") && !joint.getName().equals("Chest") && !joint.getName().equals("Torso"));
        } else {
            this.transformer.setFilter(null);
        }
    }

    public boolean isFirstPerson() {
        return isFirstPerson;
    }

    public EpicFlightModel getModel() {
        return model;
    }

    public BakedArmatureTransformer getTransformer() {
        return transformer;
    }

    public SkinOverriddenManager getOverriddenManager() {
        return context.getRenderData().getOverriddenManager();
    }

    public void setPose(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    public PoseStack getPose() {
        return poseStack;
    }
}
