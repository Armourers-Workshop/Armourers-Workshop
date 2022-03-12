package moe.plushie.armourers_workshop.core.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.block.HologramProjectorTileEntity;
import moe.plushie.armourers_workshop.core.color.ColorScheme;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.render.renderer.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.renderer.SkinRendererManager;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class HologramProjectorEntityRenderer<T extends HologramProjectorTileEntity> extends TileEntityRenderer<T> {

    public HologramProjectorEntityRenderer(TileEntityRendererDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, int overlay) {
        ItemStack itemStack = entity.getItem(0);
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(SkinDescriptor.of(itemStack));
        if (bakedSkin == null) {
            return;
        }
        Entity mannequin = SkinItemRenderer.getItemStackRenderer().getMannequinEntity();
        BipedModel<?> model = SkinItemRenderer.getItemStackRenderer().getMannequinModel();
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(mannequin);
        if (renderer == null || mannequin == null || mannequin.level == null) {
            return;
        }
        float f = 1 / 16f;
        float partialTicks1 = System.currentTimeMillis() % 100000000;
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

        Rectangle3f rect = bakedSkin.getRenderBounds(mannequin, model, null);
        entity.setRenderBoundingBoxWithRect(rect);
        apply(entity, rect, partialTicks1, matrixStack, buffers);

        SkinRenderBuffer buffer1 = SkinRenderBuffer.getInstance();
        renderer.render(mannequin, model, bakedSkin, ColorScheme.EMPTY, ItemCameraTransforms.TransformType.NONE, overLight, partialTicks1, matrixStack, buffer1);
        buffer1.endBatch();

        matrixStack.popPose();

        if (AWConfig.showDebugHologramProjector) {
            BlockPos pos = entity.getBlockPos();
            matrixStack.pushPose();
            matrixStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            RenderUtils.drawBoundingBox(matrixStack, entity.getRenderBoundingBox(), Color.ORANGE, buffers);
            matrixStack.popPose();
        }
    }

    private void apply(T entity, Rectangle3f rect, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        Vector3f angle = entity.getModelAngle();
        Vector3f offset = entity.getModelOffset();
        Vector3f rotationOffset = entity.getRotationOffset();
        Vector3f rotationSpeed = entity.getRotationSpeed();

        float rotX = angle.x();
        float speedX = rotationSpeed.x();
        if (speedX != 0) {
            rotX += ((partialTicks % speedX) / speedX) * 360.0f;
        }

        float rotY = angle.y();
        float speedY = rotationSpeed.y();
        if (speedY != 0) {
            rotY += ((partialTicks % speedY) / speedY) * 360.0f;
        }

        float rotZ = angle.z();
        float speedZ = rotationSpeed.z();
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

        if (AWConfig.showDebugHologramProjector) {
            RenderUtils.drawPoint(matrixStack, null, 128, buffers);
        }

        matrixStack.mulPose(new Quaternion(rotX, -rotY, rotZ, true));
        matrixStack.translate(rotationOffset.x(), -rotationOffset.y(), rotationOffset.z());

        if (AWConfig.showDebugHologramProjector) {
            RenderUtils.drawPoint(matrixStack, null, 128, buffers);
        }
    }
}
