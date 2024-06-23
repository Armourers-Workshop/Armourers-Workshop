package moe.plushie.armourers_workshop.core.client.skinrender.patch;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class LivingEntityRenderPatch<T extends LivingEntity> extends EntityRenderPatch<T> {

    protected EntityModel<?> entityModel;

    public LivingEntityRenderPatch(EntityRenderData renderData) {
        super(renderData);
    }

    public static <T extends LivingEntity> void activate(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, LivingEntityRenderer<?, ?> entityRenderer, Consumer<LivingEntityRenderPatch<T>> handler) {
        // create a new patch.
        _activate(LivingEntityRenderPatch.class, entity, partialTicks, packedLight, poseStackIn, buffersIn, entityRenderer, handler, LivingEntityRenderPatch::new);
    }

    public static <T extends LivingEntity> void apply(T entity, Consumer<LivingEntityRenderPatch<T>> handler) {
        _apply(LivingEntityRenderPatch.class, entity, handler);
    }

    public static <T extends LivingEntity> void deactivate(T entity, Consumer<LivingEntityRenderPatch<T>> handler) {
        _deactivate(LivingEntityRenderPatch.class, entity, handler);
    }

    @Override
    protected final void onInit(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, EntityRenderer<?> entityRenderer) {
        if (entityRenderer instanceof LivingEntityRenderer) {
            onInit(entity, partialTicks, packedLight, poseStackIn, buffersIn, (LivingEntityRenderer<?, ?>) entityRenderer);
        }
    }

    protected void onInit(T entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, LivingEntityRenderer<?, ?> entityRenderer) {
        super.onInit(entity, partialTicks, packedLight, poseStackIn, buffersIn, entityRenderer);
        var entityModel = entityRenderer.getModel();
        if (this.entityModel != entityModel) {
            this.entityModel = entityModel;
            this.transformer = BakedArmatureTransformer.defaultBy(entity, entityModel, entityRenderer);
        }
    }
}
