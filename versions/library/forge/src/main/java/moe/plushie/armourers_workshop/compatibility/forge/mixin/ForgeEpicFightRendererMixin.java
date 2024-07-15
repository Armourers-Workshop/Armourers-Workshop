package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeEpicFightHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Available("[1.18, )")
@Pseudo
@Mixin(PatchedLivingEntityRenderer.class)
public abstract class ForgeEpicFightRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At("HEAD"), remap = false)
    public void aw2$renderPre(LivingEntity entityIn, LivingEntityPatch<?> entityPatch, LivingEntityRenderer<?, ?> renderer, MultiBufferSource bufferSourceIn, PoseStack poseStackIn, int packedLightIn, float partialTicks, CallbackInfo ci) {
        AbstractForgeEpicFightHandler.onRenderPre(entityIn, packedLightIn, partialTicks, false, poseStackIn, bufferSourceIn, renderer);
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/client/model/AnimatedMesh;drawModelWithPose(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IFFFFILyesman/epicfight/api/model/Armature;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;)V", remap = false), remap = false)
    public void aw2$renderEntity(AnimatedMesh mesh, PoseStack poseStackIn, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord, Armature armature, OpenMatrix4f[] poses, LivingEntity entityIn) {
        var cir = new CallbackInfoReturnable<>("poses", true, poses);
        AbstractForgeEpicFightHandler.onRenderEntity(entityIn, armature, packedLightIn, 0, poseStackIn, null, cir);
        mesh.drawModelWithPose(poseStackIn, builder, packedLightIn, r, g, b, a, overlayCoord, armature, cir.getReturnValue());
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At("RETURN"), remap = false)
    public void aw2$renderPost(LivingEntity entityIn, LivingEntityPatch<?> entityPatch, LivingEntityRenderer<?, ?> renderer, MultiBufferSource bufferSourceIn, PoseStack poseStackIn, int packedLightIn, float partialTicks, CallbackInfo ci) {
        AbstractForgeEpicFightHandler.onRenderPost(entityIn, packedLightIn, partialTicks, poseStackIn, bufferSourceIn, renderer);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void aw2$init(CallbackInfo callbackInfo) {
        AbstractForgeEpicFightHandler.onInit();
    }
}
