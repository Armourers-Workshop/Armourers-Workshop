package moe.plushie.armourers_workshop.compat.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Conditional;
import moe.plushie.armourers_workshop.compat.forge.AbstractForgeEpicFightHandler;
import moe.plushie.armourers_workshop.core.client.render.layer.SkinWardrobeLayer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Available("[1.20, 1.22)")
@Conditional("epicfight")
@Pseudo
@Mixin(PatchedLivingEntityRenderer.class)
public abstract class ForgeEpicFightRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At("HEAD"), remap = false)
    public void aw2$renderPre(LivingEntity entityIn, LivingEntityPatch<?> entityPatch, LivingEntityRenderer<?, ?> renderer, MultiBufferSource bufferSourceIn, PoseStack poseStackIn, int packedLightIn, float partialTick, CallbackInfo ci) {
        AbstractForgeEpicFightHandler.onRenderPre(entityIn, packedLightIn, partialTick, false, poseStackIn, bufferSourceIn, renderer);
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/client/model/SkinnedMesh;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;IFFFFILyesman/epicfight/api/model/Armature;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;)V", remap = false), remap = false)
    public void aw2$renderEntity(SkinnedMesh mesh, PoseStack poseStackIn, MultiBufferSource bufferSource, RenderType renderType, int packedLightIn, float r, float g, float b, float a, int overlayCoord, Armature armature, OpenMatrix4f[] poses, LivingEntity entityIn) {
        var cir = new CallbackInfoReturnable<>("poses", true, poses);
        AbstractForgeEpicFightHandler.onRenderEntity(entityIn, armature, packedLightIn, 0, poseStackIn, bufferSource, cir);
        mesh.draw(poseStackIn, bufferSource, renderType, packedLightIn, r, g, b, a, overlayCoord, armature, cir.getReturnValue());
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At("RETURN"), remap = false)
    public void aw2$renderPost(LivingEntity entityIn, LivingEntityPatch<?> entityPatch, LivingEntityRenderer<?, ?> renderer, MultiBufferSource bufferSourceIn, PoseStack poseStackIn, int packedLightIn, float partialTick, CallbackInfo ci) {
        AbstractForgeEpicFightHandler.onRenderPost(entityIn, packedLightIn, partialTick, poseStackIn, bufferSourceIn, renderer);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void aw2$init(CallbackInfo callbackInfo) {
        AbstractForgeEpicFightHandler.onInit();
        var entityRenderer = PatchedLivingEntityRenderer.class.cast(this);
        entityRenderer.addPatchedLayer(SkinWardrobeLayer.class, new EmptyLayer<>() {
            @Override
            protected void renderLayer(LivingEntityPatch<LivingEntity> entityPatch, LivingEntity entityIn, RenderLayer<LivingEntity, EntityModel<LivingEntity>> originalLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLightIn, OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTick) {
                originalLayer.render(poseStack, buffer, packedLightIn, entityIn, partialTick, 0, partialTick, packedLightIn, xRot, yRot);
            }
        });
    }
}
