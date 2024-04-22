package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

import manifold.ext.rt.api.auto;

public class LivingEntityRenderPatch<T extends LivingEntity> extends EntityRenderPatch<T> {

    private EntityModel<?> entityModel;
    private final LivingEntityRenderer<?, ?> entityRenderer;

    public LivingEntityRenderPatch(SkinRenderData renderData, LivingEntityRenderer<?, ?> entityRenderer) {
        super(renderData);
        this.entityRenderer = entityRenderer;
    }

    public static <T extends LivingEntity> void activate(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, LivingEntityRenderer<?, ?> entityRenderer, Consumer<LivingEntityRenderPatch<T>> handler) {
        _activate(LivingEntityRenderPatch.class, entity, partialTicks, packedLight, poseStackIn, buffersIn, handler, renderData -> {
            // create a new patch.
            return new LivingEntityRenderPatch<>(renderData, entityRenderer);
        });
    }

    public static <T extends LivingEntity> void apply(T entity, Consumer<LivingEntityRenderPatch<T>> handler) {
        _apply(LivingEntityRenderPatch.class, entity, handler);
    }

    public static <T extends LivingEntity> void deactivate(T entity, Consumer<LivingEntityRenderPatch<T>> handler) {
        _deactivate(LivingEntityRenderPatch.class, entity, handler);
    }

    @Override
    protected void onInit(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn) {
        super.onInit(entity, partialTicks, packedLight, poseStackIn, buffersIn);
        auto entityModel = entityRenderer.getModel();
        if (this.entityModel != entityModel) {
            this.entityModel = entityModel;
            this.transformer = BakedArmatureTransformer.defaultBy(entity, entityModel, entityRenderer);
        }
    }
}
