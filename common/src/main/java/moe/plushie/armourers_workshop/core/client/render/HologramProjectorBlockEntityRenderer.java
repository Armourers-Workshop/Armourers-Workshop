package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRendererContext;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.*;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

@Environment(value = EnvType.CLIENT)
public class HologramProjectorBlockEntityRenderer<T extends HologramProjectorBlockEntity> extends AbstractBlockEntityRenderer<T> {

    public HologramProjectorBlockEntityRenderer(AbstractBlockEntityRendererContext context) {
        super(context);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        if (!entity.isPowered()) {
            return;
        }
        ItemStack itemStack = entity.getItem(0);
        BakedSkin bakedSkin = BakedSkin.of(itemStack);
        if (bakedSkin == null) {
            return;
        }
        BlockState blockState = entity.getBlockState();
        Entity mannequin = SkinItemRenderer.getInstance().getMannequinEntity();
        MannequinModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(mannequin, model, null);
        if (renderer == null || mannequin == null || mannequin.level == null) {
            return;
        }
        float f = 1 / 16f;
        float partialTicks1 = TickUtils.ticks();
        int overLight = light;
        if (entity.isOverrideLight()) {
            overLight = 0xf000f0;
        }

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(entity.getRenderRotations(blockState));
        poseStack.translate(0.0f, 0.5f, 0.0f);

        poseStack.scale(f, f, f);
        poseStack.scale(-1, -1, 1);

        Rectangle3f rect = bakedSkin.getRenderBounds(mannequin, model, null, itemStack);
        apply(entity, rect, partialTicks1, poseStack, buffers);

        SkinRenderContext context = SkinRenderContext.getInstance();
        context.setup(overLight, partialTicks1, poseStack, buffers);
        renderer.render(mannequin, SkinRendererManager.wrap(model), bakedSkin, ColorScheme.EMPTY, itemStack, 0, context);

        poseStack.popPose();

        if (ModDebugger.hologramProjectorBlock) {
            BlockPos pos = entity.getBlockPos();
            poseStack.pushPose();
            poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            RenderSystem.drawBoundingBox(poseStack, entity.getCustomRenderBoundingBox(blockState), UIColor.ORANGE, buffers);
            poseStack.popPose();
        }
    }

    private void apply(T entity, Rectangle3f rect, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers) {
        Vector3f angle = entity.getModelAngle();
        Vector3f offset = entity.getModelOffset();
        Vector3f rotationOffset = entity.getRotationOffset();
        Vector3f rotationSpeed = entity.getRotationSpeed();

        float rotX = angle.getX();
        float speedX = rotationSpeed.getX();
        if (speedX != 0) {
            rotX += ((partialTicks % speedX) / speedX) * 360.0f;
        }

        float rotY = angle.getY();
        float speedY = rotationSpeed.getY();
        if (speedY != 0) {
            rotY += ((partialTicks % speedY) / speedY) * 360.0f;
        }

        float rotZ = angle.getZ();
        float speedZ = rotationSpeed.getZ();
        if (speedZ != 0) {
            rotZ += ((partialTicks % speedZ) / speedZ) * 360.0f;
        }

        float scale = entity.getModelScale();
        matrixStack.scale(scale, scale, scale);
        if (entity.isOverrideOrigin()) {
            matrixStack.translate(0, -rect.getMaxY(), 0); // to model center
        }
        matrixStack.translate(-offset.x(), -offset.y(), offset.z());

        if (entity.shouldShowRotationPoint()) {
            RenderSystem.drawBoundingBox(matrixStack, -1, -1, -1, 1, 1, 1, UIColor.MAGENTA, buffers);
        }

        if (ModDebugger.hologramProjectorBlock) {
            RenderSystem.drawPoint(matrixStack, null, 128, buffers);
        }

        matrixStack.mulPose(TrigUtils.rotate(rotX, -rotY, rotZ, true));
        matrixStack.translate(rotationOffset.x(), -rotationOffset.y(), rotationOffset.z());

        if (ModDebugger.hologramProjectorBlock) {
            RenderSystem.drawPoint(matrixStack, null, 128, buffers);
        }
    }
}
