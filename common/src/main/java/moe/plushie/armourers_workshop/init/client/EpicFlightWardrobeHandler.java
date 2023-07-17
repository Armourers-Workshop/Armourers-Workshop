package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.armature.JointTransformModifier;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightContext;
import moe.plushie.armourers_workshop.core.armature.thirdparty.EpicFlightTransformProvider;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix3f;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.Collections;

@Environment(EnvType.CLIENT)
public class EpicFlightWardrobeHandler {

    private static final EpicFlightContext context = new EpicFlightContext();

    public static void onSetup() {
        //
        ModConfig.Client.enablePartSubdivide = true;
    }

    public static void onRenderLivingPre(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer, boolean isFirstPersonRenderer, EpicFlightTransformProvider transformProvider) {
        IModel model = ModelHolder.ofNullable(entityRenderer.getModel());
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        Collection<ISkinPartType> overrideParts = null;
        if (isFirstPersonRenderer) {
            overrideParts = Collections.singleton(SkinPartTypes.BIPPED_HEAD);
        }

        model.setAssociatedObject(EpicFlightTransformProvider.KEY, transformProvider);

        context.overrideParts = overrideParts;
        context.overridePostStack = poseStack.copy();
        context.overrideTransformModifier = model.getAssociatedObject(JointTransformModifier.EPICFIGHT);
        context.isLimitLimbs = false;

        renderData.epicFlightContext = context;

        SkinRendererManager.getInstance().willRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
    }

    public static void onRenderLivingPost(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        IModel model = ModelHolder.ofNullable(entityRenderer.getModel());
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        model.setAssociatedObject(EpicFlightTransformProvider.KEY, null);

        context.overrideParts = null;
        context.overridePostStack = null;
        context.overrideTransformModifier = null;
        context.isLimitLimbs = true;

        renderData.epicFlightContext = null;

        SkinRendererManager.getInstance().didRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
    }

    public static void applyPoseFromBuffer(IPoseStack poseStack, FloatBuffer pose, FloatBuffer normal) {
        poseStack.multiply(new OpenMatrix3f(normal));
        poseStack.multiply(new OpenMatrix4f(pose));
    }
}
