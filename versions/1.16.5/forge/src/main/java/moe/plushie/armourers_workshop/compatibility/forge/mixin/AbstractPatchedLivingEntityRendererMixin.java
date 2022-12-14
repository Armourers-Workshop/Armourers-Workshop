package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.armature.JointTransformBuilder;
import moe.plushie.armourers_workshop.core.armature.ModelBinder;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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


    @Inject(method = "renderLayer", at = @At("HEAD"))
    public void aw$renderLayerPre(LivingEntityRenderer<?, ?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStackIn, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        SkinRenderData renderData = SkinRenderData.of(entityIn);
        if (renderData == null) {
            return;
        }
        Armature armature = entityPatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature();
        JointTransformBuilder builder = JointTransformBuilder.of(Armatures.BIPPED);
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);

        ModelBinder.BIPPED.forEach((joint2, binder) -> {
            String name = joint2.getName();
            if (binder.name != null) {
                name = binder.name;
            }
            Joint joint = armature.searchJointByName(name);
            if (joint == null) {
                return;
            }
            OpenMatrix4f m4 = new OpenMatrix4f();
            m4.mulBack(poses[joint.getId()]);
            m4.translate(binder.x / 16, binder.y / 16, binder.z / 16);
            PoseStack fixedPoseStack = new PoseStack();
            fixedPoseStack.last().pose().multiply(OpenMatrix4f.exportToMojangMatrix(m4));
            IPoseStack animatedTransform = AbstractPoseStack.wrap(fixedPoseStack);
            builder.put(joint2, poseStack1 -> {
                float f1 = 16f;
                float f2 = 1 / 16f;
                poseStack1.scale(f1, f1, f1);
                poseStack1.multiply(animatedTransform);
                poseStack1.scale(-f2, -f2, f2);
            });
        });

        Collection<ISkinPartType> overrideParts = null;
        if (ObjectUtils.safeCast(this, FirstPersonRenderer.class) != null) {
            overrideParts = Collections.singleton(SkinPartTypes.BIPPED_HEAD);
        }
        renderData.overrideParts = overrideParts;
        renderData.overridePostStack = poseStack.copy();
        renderData.overrideTransforms = builder.build();
    }

    @Inject(method = "renderLayer", at = @At("RETURN"))
    public void aw$renderLayerPost(LivingEntityRenderer<?, ?> renderer, LivingEntityPatch<?> entityPatch, LivingEntity entityIn, OpenMatrix4f[] poses, MultiBufferSource buffers, PoseStack poseStack, int packedLightIn, float partialTicks, CallbackInfo callbackInfo) {
        SkinRenderData renderData = SkinRenderData.of(entityIn);
        if (renderData == null) {
            return;
        }
        renderData.overrideParts = null;
        renderData.overridePostStack = null;
        renderData.overrideTransforms = null;
    }

    @Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true)
    public void aw$getRenderType(LivingEntity entityIn, LivingEntityPatch<?> entityPatch, LivingEntityRenderer<?, ?> renderer, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing, CallbackInfoReturnable<RenderType> callbackInfo) {
        if (entityIn instanceof Player) {
            callbackInfo.setReturnValue(null);
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void aw$init(CallbackInfo callbackInfo) {
        //
        ModConfig.Client.enablePartSubdivide = true;
    }
}
