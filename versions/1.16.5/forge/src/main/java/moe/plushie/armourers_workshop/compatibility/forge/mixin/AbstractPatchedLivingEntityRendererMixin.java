package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IOverrideModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.HashMap;

@Mixin(PatchedLivingEntityRenderer.class)
public abstract class AbstractPatchedLivingEntityRendererMixin {

    @Inject(method = "renderLayer", at = @At("HEAD"), cancellable = true)
    public void aw$renderLayerPre(LivingEntityRenderer<?,?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStackIn, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        IOverrideModelHolder parentModel = ObjectUtils.safeCast(SkinRendererManager.wrap(renderer.getModel()), IOverrideModelHolder.class);
        if (parentModel == null) {
            return;
        }
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        ClientModel clientModel = entityPatch.getEntityModel(ClientModels.LOGICAL_CLIENT);
        Armature armature = clientModel.getArmature();

        HashMap<String, IPoseStack> overrides = new HashMap<>();
        moe.plushie.armourers_workshop.core.armature.Armature.BOXES.forEach((k, v) -> {
            String name = k;
            if (v.binding != null) {
                name = v.binding;
            }
            Joint joint = armature.searchJointByName(name);
            if (joint == null) {
                return;
            }
            OpenMatrix4f m4 = new OpenMatrix4f();
            m4.mulBack(poses[joint.getId()]);
            m4.translate(v.o.getX() / 16f, v.o.getY() / 16f, v.o.getZ() / 16f);
            PoseStack fixedPoseStack = new PoseStack();
            fixedPoseStack.last().pose().multiply(OpenMatrix4f.exportToMojangMatrix(m4));
            overrides.put(k, AbstractPoseStack.wrap(fixedPoseStack));
        });
        parentModel.setOverrides(overrides);

        if (ModDebugger.armature) {
            moe.plushie.armourers_workshop.core.armature.Armature.renderTest(overrides, buffers, poseStack, packedLightIn, partialTicks);
        }

        SkinRenderData renderData = SkinRenderData.of(entityIn);
        if (renderData != null) {
            IPoseStack overridePostStack = AbstractPoseStack.empty();
            overridePostStack.multiply(poseStack);
            renderData.overridePostStack = overridePostStack;
        }
    }

    @Inject(method = "renderLayer", at = @At("RETURN"))
    public void aw$renderLayerPost(LivingEntityRenderer<?,?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStack, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        IOverrideModelHolder parentModel = ObjectUtils.safeCast(SkinRendererManager.wrap(renderer.getModel()), IOverrideModelHolder.class);
        if (parentModel == null) {
            return;
        }
        parentModel.setOverrides(null);
        SkinRenderData renderData = SkinRenderData.of(entityIn);
        if (renderData != null) {
            renderData.overridePostStack = null;
        }
    }

    @Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true)
    public void aw$getRenderType(LivingEntity entityIn, LivingEntityPatch<?> entityPatch, LivingEntityRenderer<?, ?> renderer, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing, CallbackInfoReturnable<RenderType> callbackInfo) {
        callbackInfo.setReturnValue(null);
    }
}
