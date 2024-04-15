package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

import manifold.ext.rt.api.auto;

public class LivingEntityRenderPatch extends EntityRenderPatch<LivingEntity> {

    private BakedArmatureTransformer transformer;
    private EntityModel<?> model;
    private final LivingEntityRenderer<?, ?> entityRenderer;

    public LivingEntityRenderPatch(SkinRenderData renderData, LivingEntityRenderer<?, ?> entityRenderer) {
        super(renderData);
        this.entityRenderer = entityRenderer;
    }

    public static void activate(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, LivingEntityRenderer<?, ?> entityRenderer) {
        auto renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        auto renderPatch = renderData.getRenderPatch();
        if (renderPatch == null) {
            auto renderPatch1 = new LivingEntityRenderPatch(renderData, entityRenderer);
            renderPatch = ObjectUtils.unsafeCast(renderPatch1);
            renderData.setRenderPatch(renderPatch);
        }

        renderPatch.onInit(entity, partialTicks, packedLight, poseStackIn, buffersIn);
        renderPatch.onActivate(entity);
    }

    public static void render(LivingEntity entity) {
        auto renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            auto renderPatch = renderData.getRenderPatch();
            if (renderPatch != null) {
                renderPatch.onRender(entity);
            }
        }
    }

    public static void deactivate(LivingEntity entity) {
        auto renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            auto renderPatch = renderData.getRenderPatch();
            if (renderPatch != null) {
                renderPatch.onDeactivate(entity);
            }
        }
    }

    @Override
    protected void onInit(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn) {
        super.onInit(entity, partialTicks, packedLight, poseStackIn, buffersIn);
        auto model = entityRenderer.getModel();
        if (this.model != model) {
            this.model = model;
            this.transformer = BakedArmatureTransformer.defaultBy(entity, model, entityRenderer);
        }
    }

    @Override
    protected void onActivate(LivingEntity entity) {
        if (this.transformer != null) {
            this.transformer.prepare(entity, this);
        }
    }

    @Override
    protected void onDeactivate(LivingEntity entity) {
        if (this.transformer != null) {
            this.transformer.deactivate(entity, this);
        }
    }

    @Override
    protected void onRender(LivingEntity entity) {
        if (this.transformer != null) {
            this.transformer.activate(entity, this);
        }
    }
}
