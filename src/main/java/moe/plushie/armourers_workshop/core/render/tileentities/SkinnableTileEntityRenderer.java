package moe.plushie.armourers_workshop.core.render.tileentities;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.item.SkinItemStackRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRenderer;
import moe.plushie.armourers_workshop.core.render.skin.SkinRendererManager;
import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
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
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class SkinnableTileEntityRenderer<T extends SkinnableTileEntity> extends TileEntityRenderer<T> {

    public SkinnableTileEntityRenderer(TileEntityRendererDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, int overlay) {
        BakedSkin bakedSkin = BakedSkin.of(entity.getDescriptor());
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
        Quaternion rotations = entity.getRenderRotations();

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(rotations);

        matrixStack.scale(f, f, f);
        matrixStack.scale(-1, -1, 1);

        renderer.render(mannequin, model, bakedSkin, ColorScheme.EMPTY, ItemCameraTransforms.TransformType.NONE, light, partialTicks1, matrixStack, buffers);

        matrixStack.popPose();

        if (ModConfig.Client.debugSkinnableBlock) {
            bakedSkin.getBlockBounds().forEach((pos, rect) -> {
                matrixStack.pushPose();
                matrixStack.translate(0.5f, 0.5f, 0.5f);
                matrixStack.scale(f, f, f);
                matrixStack.mulPose(rotations);
                matrixStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
                RenderUtils.drawBoundingBox(matrixStack, rect, Color.RED, buffers);
                matrixStack.popPose();
            });
            BlockPos pos = entity.getBlockPos();
            matrixStack.pushPose();
            matrixStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());
            RenderUtils.drawBoundingBox(matrixStack, entity.getRenderBoundingBox(), Color.ORANGE, buffers);
            matrixStack.popPose();
        }
    }
}
