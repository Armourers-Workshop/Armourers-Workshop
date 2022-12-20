package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.armature.JointTransformModifier;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightTransformProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
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

import java.util.Collection;
import java.util.Collections;

@Pseudo
@Mixin(PatchedLivingEntityRenderer.class)
public abstract class AbstractPatchedLivingEntityRendererMixin {

    @Inject(method = "renderLayer", at = @At("HEAD"), remap = false)
    public void aw$renderLayerPre(LivingEntityRenderer<?, ?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStackIn, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        IModelHolder<?> model = ModelHolder.ofNullable(renderer.getModel());
        SkinRenderData renderData = SkinRenderData.of(entityIn);
        if (renderData == null) {
            return;
        }
        Armature armature = entityPatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature();
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        JointTransformModifier transformModifier = model.getExtraData(JointTransformModifier.EPICFIGHT);
        ITransformf[] transforms = transformModifier.getTransforms(entityIn.getType(), model);

        model.setExtraData(EpicFlightTransformProvider.KEY, name -> {
            Joint joint = armature.searchJointByName(name);
            if (joint == null) {
                return ITransformf.NONE;
            }
            return poseStack1 -> {
                PoseStack poseStack2 = poseStack1.cast();
                poseStack2.last().pose().multiply(OpenMatrix4f.exportToMojangMatrix(poses[joint.getId()]));
            };
        });

        Collection<ISkinPartType> overrideParts = null;
        if (ObjectUtils.safeCast(this, FirstPersonRenderer.class) != null) {
            overrideParts = Collections.singleton(SkinPartTypes.BIPPED_HEAD);
        }

        renderData.overrideParts = overrideParts;
        renderData.overridePostStack = poseStack.copy();
        renderData.overrideTransforms = transforms;
    }

    @Inject(method = "renderLayer", at = @At("RETURN"), remap = false)
    public void aw$renderLayerPost(LivingEntityRenderer<?, ?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStackIn, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        IModelHolder<?> model = ModelHolder.ofNullable(renderer.getModel());
        SkinRenderData renderData = SkinRenderData.of(entityIn);
        if (renderData == null) {
            return;
        }
        renderData.overrideParts = null;
        renderData.overridePostStack = null;
        renderData.overrideTransforms = null;
        model.setExtraData(EpicFlightTransformProvider.KEY, null);
    }

    @Inject(method = "getRenderType", at = @At("RETURN"), remap = false, cancellable = true)
    public void aw$getRenderType(LivingEntity entityIn, LivingEntityPatch<?> entityPatch, LivingEntityRenderer<?, ?> renderer, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing, CallbackInfoReturnable<RenderType> callbackInfo) {
        SkinRenderData renderData = SkinRenderData.of(entityIn);
        if (renderData == null) {
            return;
        }
        if (renderData.getOverriddenManager().hasAnyPartOverride()) {
            callbackInfo.setReturnValue(null);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void aw$init(CallbackInfo callbackInfo) {
        //
        ModConfig.Client.enablePartSubdivide = true;
    }
}
