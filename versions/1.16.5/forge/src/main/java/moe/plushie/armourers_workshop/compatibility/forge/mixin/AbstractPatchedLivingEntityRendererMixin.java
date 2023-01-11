package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.init.client.EpicFlightWardrobeHandler;
import moe.plushie.armourers_workshop.utils.MatrixUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.FirstPersonRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.nio.FloatBuffer;

@Pseudo
@Mixin(PatchedLivingEntityRenderer.class)
public abstract class AbstractPatchedLivingEntityRendererMixin {

    private static final FloatBuffer AW_MAT_BUFFER3 = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer AW_MAT_BUFFER4 = BufferUtils.createFloatBuffer(16);

    @Inject(method = "renderLayer", at = @At("HEAD"), remap = false)
    public void aw$renderLayerPre(LivingEntityRenderer<?, ?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStackIn, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        Armature armature = entityPatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature();
        IPoseStack poseStack = MatrixUtils.of(poseStackIn);
        boolean isFirstPersonRenderer = ObjectUtils.safeCast(this, FirstPersonRenderer.class) != null;
        EpicFlightWardrobeHandler.onRenderLivingPre(entityIn, partialTicks, packedLightIn, poseStack, buffers, renderer, isFirstPersonRenderer, name -> {
            Joint joint = armature.searchJointByName(name);
            if (joint == null) {
                return ITransformf.NONE;
            }
            return poseStack1 -> {
                OpenMatrix4f jointMatrix = poses[joint.getId()];
                OpenMatrix4f jointNormalMatrix = jointMatrix.removeTranslation();
                jointMatrix.store(AW_MAT_BUFFER4);
                jointNormalMatrix.store(AW_MAT_BUFFER3);
                AW_MAT_BUFFER3.position(0);
                AW_MAT_BUFFER4.position(0);
                poseStack1.lastPose().multiply(MatrixUtils.mat4(AW_MAT_BUFFER4));
                poseStack1.lastNormal().multiply(MatrixUtils.mat3(AW_MAT_BUFFER3));
            };
        });
    }

    @Inject(method = "renderLayer", at = @At("RETURN"), remap = false)
    public void aw$renderLayerPost(LivingEntityRenderer<?, ?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStackIn, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        IPoseStack poseStack = MatrixUtils.of(poseStackIn);
        EpicFlightWardrobeHandler.onRenderLivingPost(entityIn, partialTicks, packedLightIn, poseStack, buffers, renderer);
    }

    @Inject(method = "getRenderType", at = @At("RETURN"), remap = false, cancellable = true)
    public void aw$getRenderType(LivingEntity entityIn, LivingEntityPatch<?> entityPatch, LivingEntityRenderer<?, ?> renderer, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing, CallbackInfoReturnable<RenderType> callbackInfo) {
        SkinRenderData renderData = SkinRenderData.of(entityIn);
        if (renderData != null && renderData.getOverriddenManager().overrideAnyModel()) {
            callbackInfo.setReturnValue(null);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void aw$init(CallbackInfo callbackInfo) {
        EpicFlightWardrobeHandler.onSetup();
    }
}
