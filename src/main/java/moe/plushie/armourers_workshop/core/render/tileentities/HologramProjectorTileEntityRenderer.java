package moe.plushie.armourers_workshop.core.render.tileentities;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.item.SkinItemStackRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.tileentity.HologramProjectorTileEntity;
import moe.plushie.armourers_workshop.utils.Rectangle3f;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class HologramProjectorTileEntityRenderer<T extends HologramProjectorTileEntity> extends TileEntityRenderer<T> {

    public HologramProjectorTileEntityRenderer(TileEntityRendererDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, int overlay) {
        if (!entity.isPowered()) {
            return;
        }
        BakedSkin bakedSkin = BakedSkin.of(entity.getItem(0));
        if (bakedSkin == null) {
            return;
        }
        Entity mannequin = SkinItemStackRenderer.getInstance().getMannequinEntity();
        BipedModel<?> model = SkinItemStackRenderer.getInstance().getMannequinModel();
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
        apply(entity, rect, partialTicks1, matrixStack, buffers);

        renderer.render(mannequin, model, bakedSkin, ColorScheme.EMPTY, ItemCameraTransforms.TransformType.NONE, overLight, partialTicks1, matrixStack, buffers);

        matrixStack.popPose();

        if (ModConfig.Client.debugHologramProjectorBlock) {
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

        if (ModConfig.Client.debugHologramProjectorBlock) {
            RenderUtils.drawPoint(matrixStack, null, 128, buffers);
        }

        matrixStack.mulPose(TrigUtils.rotate(rotX, -rotY, rotZ, true));
        matrixStack.translate(rotationOffset.x(), -rotationOffset.y(), rotationOffset.z());

        if (ModConfig.Client.debugHologramProjectorBlock) {
            RenderUtils.drawPoint(matrixStack, null, 128, buffers);
        }
    }
}
