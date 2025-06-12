package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightTransformProvider;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRendererContext;
import moe.plushie.armourers_workshop.core.client.other.thirdparty.EpicFlightModel;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class EpicFightEntityRendererPatch<T extends LivingEntity> extends EntityRenderPatch<T> {

    private EntityModel<?> entityModel;

    private IPoseStack overridePoseStack;
    private EpicFlightModel transformerModel;
    private EpicFlightTransformProvider transformProvider;

    public EpicFightEntityRendererPatch(EntityRenderData renderData, EntityRendererContext rendererContext) {
        super(renderData, rendererContext);
    }

    public static <T extends LivingEntity> void activate(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, LivingEntityRenderer<?, ?> entityRenderer, Consumer<EpicFightEntityRendererPatch<T>> handler) {
        _activate(EpicFightEntityRendererPatch.class, entity, partialTicks, packedLight, poseStackIn, entityRenderer, handler, (renderData, rendererContext) -> {
            var model = EpicFlightModel.ofNullable(entityRenderer.getModel());
            if (model != null) {
                return new EpicFightEntityRendererPatch<>(renderData, rendererContext);
            }
            return null;
        });
    }

    public static <T extends LivingEntity> void apply(T entity, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, Consumer<EpicFightEntityRendererPatch<T>> handler) {
        _apply(EpicFightEntityRendererPatch.class, entity, poseStackIn, bufferSourceIn, handler);
    }

    public static <T extends LivingEntity> void deactivate(T entity, Consumer<EpicFightEntityRendererPatch<T>> handler) {
        _deactivate(EpicFightEntityRendererPatch.class, entity, handler);
    }

    @Override
    protected final void onInit(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, EntityRenderer<?> entityRenderer) {
        if (entityRenderer instanceof LivingEntityRenderer) {
            onInit(entity, partialTicks, packedLight, poseStackIn, (LivingEntityRenderer<?, ?>) entityRenderer);
        }
    }

    protected void onInit(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, LivingEntityRenderer<?, ?> entityRenderer) {
        super.onInit(entity, partialTicks, packedLight, poseStackIn, entityRenderer);
        var entityModel = entityRenderer.getModel();
        if (this.entityModel != entityModel) {
            this.entityModel = entityModel;
            this.transformerModel = EpicFlightModel.ofNullable(entityModel);
            this.transformer = createTransformer(entity, transformerModel, entityRenderer);
        }
    }


    public void setFirstPerson(boolean isFirstPerson) {
        if (transformer == null) {
            return;
        }
        if (isFirstPerson) {
            transformer.setFilter(joint -> !joint.name().equals("Head") && !joint.name().equals("Chest") && !joint.name().equals("Torso"));
        } else {
            transformer.setFilter(null);
        }
    }

    public void setMesh(Object mesh) {
        if (transformerModel != null) {
            transformerModel.linkTo(mesh);
        }
    }

    public void setTransformProvider(EpicFlightTransformProvider newTransformProvider) {
        transformProvider = newTransformProvider;
        if (transformerModel != null) {
            transformerModel.setAssociatedObject(EpicFlightTransformProvider.KEY, newTransformProvider);
        }
    }

    public EpicFlightTransformProvider getTransformProvider() {
        return transformProvider;
    }

    public void setOverridePose(IPoseStack pose) {
        overridePoseStack = pose;
    }

    public IPoseStack overridePose() {
        if (overridePoseStack != null) {
            return overridePoseStack;
        }
        return pluginContext.poseStack();
    }

    @Override
    public BakedArmatureTransformer transformer() {
        // the transformer status is abnormal when transform provider is null.
        if (transformProvider != null) {
            return super.transformer();
        }
        return null;
    }

    private BakedArmatureTransformer createTransformer(Entity entity, EpicFlightModel entityModel, LivingEntityRenderer<?, ?> entityRenderer) {
        if (entityModel != null) {
            return EntityRendererContext.of(entityRenderer).createTransformer(entityModel, SkinRendererManager.EPIC_FIGHT);
        }
        return null;
    }
}
