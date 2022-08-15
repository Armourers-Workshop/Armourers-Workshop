package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.awt.*;

@Environment(value = EnvType.CLIENT)
public class HologramProjectorBlockEntityRenderer<T extends HologramProjectorBlockEntity> extends BlockEntityRenderer<T> {

    public HologramProjectorBlockEntityRenderer(BlockEntityRenderDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int light, int overlay) {
        if (!entity.isPowered()) {
            return;
        }
        ItemStack itemStack = entity.getItem(0);
        BakedSkin bakedSkin = BakedSkin.of(itemStack);
        if (bakedSkin == null) {
            return;
        }
        Entity mannequin = SkinItemRenderer.getInstance().getMannequinEntity();
        HumanoidModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(mannequin, model, null);
        if (renderer == null || mannequin == null || mannequin.level == null) {
            return;
        }
        float f = 1 / 16f;
        float partialTicks1 = TickUtils.ticks();
        int overLight = light;
        if (entity.isOverrideLight()) {
            overLight = 0xf000f0;
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(entity.getRenderRotations());
        matrixStack.translate(0.0f, 0.5f, 0.0f);

        matrixStack.scale(f, f, f);
        matrixStack.scale(-1, -1, 1);

        Rectangle3f rect = bakedSkin.getRenderBounds(mannequin, model, null, itemStack);
        apply(entity, rect, partialTicks1, matrixStack, buffers);

        SkinRenderContext context = SkinRenderContext.getInstance();
        context.setup(overLight, partialTicks1, matrixStack, buffers);
        renderer.render(mannequin, model, bakedSkin, ColorScheme.EMPTY, itemStack, 0, context);

        matrixStack.popPose();

        if (ModDebugger.hologramProjectorBlock) {
            BlockPos pos = entity.getBlockPos();
            matrixStack.pushPose();
            matrixStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            RenderUtils.drawBoundingBox(matrixStack, entity.getCustomRenderBoundingBox(), Color.ORANGE, buffers);
            matrixStack.popPose();
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
            RenderUtils.drawBoundingBox(matrixStack, -1, -1, -1, 1, 1, 1, Color.MAGENTA, buffers);
        }

        if (ModDebugger.hologramProjectorBlock) {
            RenderUtils.drawPoint(matrixStack, null, 128, buffers);
        }

        matrixStack.mulPose(TrigUtils.rotate(rotX, -rotY, rotZ, true));
        matrixStack.translate(rotationOffset.x(), -rotationOffset.y(), rotationOffset.z());

        if (ModDebugger.hologramProjectorBlock) {
            RenderUtils.drawPoint(matrixStack, null, 128, buffers);
        }
    }
}
