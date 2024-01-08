package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.guide;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPartPose;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.skinrender.ArrowSkinRenderer;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.ModelPartPose;
import moe.plushie.armourers_workshop.utils.PoseStackWrapper;
import moe.plushie.armourers_workshop.utils.PoseUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Rotations;

import java.util.function.Function;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class AdvancedHumanGuideRenderer extends AbstractAdvancedGuideRenderer {

    private static final ImmutableMap<ISkinPartType, Function<MannequinEntity, Rotations>> RR = ImmutableMap.<ISkinPartType, Function<MannequinEntity, Rotations>>builder()
            .put(SkinPartTypes.BIPPED_LEFT_ARM, MannequinEntity::getLeftArmPose)
            .put(SkinPartTypes.BIPPED_RIGHT_ARM, MannequinEntity::getRightArmPose)

            .put(SkinPartTypes.BIPPED_LEFT_LEG, MannequinEntity::getLeftLegPose)
            .put(SkinPartTypes.BIPPED_RIGHT_LEG, MannequinEntity::getRightLegPose)

            .build();

    public AdvancedHumanGuideRenderer() {
    }

    @Override
    public void render(PoseStack poseStack, int light, int overlay, float r, float g, float b, float alpha, MultiBufferSource buffers) {
        auto entity = PlaceholderManager.MANNEQUIN.get();

        for (ISkinPartType partType : SkinDocumentTypes.GENERAL_ARMOR_OUTFIT.getSkinPartTypes()) {
            ITransformf transform = AdvancedPartOffset.MANNEQUIN_ENTITY.get(entity, partType);
            if (transform != null) {
                poseStack.pushPose();
                PoseUtils.apply(poseStack, transform);
                ShapeTesselator.point(Vector3f.ZERO, 16, poseStack, buffers);
                poseStack.popPose();
            } else {

            }
        }

        renderModel(entity, poseStack, buffers);
    }

    private void renderModel(MannequinEntity entity, PoseStack poseStack, MultiBufferSource buffers) {
        auto rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();

        poseStack.pushPose();
        poseStack.scale(-1, -1, 1);
        poseStack.translate(0.0f, -24.0f, 0.0f);

        // mannequin renderer will scale 0.9375f.
        float f = 1.0f / 0.9375f;
        poseStack.scale(16 + f, 16 + f, 16 + f);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180));

        RenderSystem.runAsFancy(() -> {
            rendererManager.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f, poseStack, buffers, 0xf000f0);
        });

        poseStack.popPose();

    }
}
