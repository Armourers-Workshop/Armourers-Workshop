package moe.plushie.armourers_workshop.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
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
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

@Environment(value = EnvType.CLIENT)
public class SkinnableBlockEntityRenderer<T extends SkinnableBlockEntity> extends BlockEntityRenderer<T> {

    public SkinnableBlockEntityRenderer(BlockEntityRenderDispatcher rendererManager) {
        super(rendererManager);
    }

    @Override
    public void render(T entity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int light, int overlay) {
        BakedSkin bakedSkin = BakedSkin.of(entity.getDescriptor());
        if (bakedSkin == null) {
            return;
        }
        BlockState blockState = entity.getBlockState();
        Entity mannequin = SkinItemRenderer.getInstance().getMannequinEntity();
        HumanoidModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model> renderer = SkinRendererManager.getInstance().getRenderer(mannequin, model, null);
        if (renderer == null || mannequin == null || mannequin.level == null) {
            return;
        }
        float f = 1 / 16f;
        float partialTicks1 = TickUtils.ticks();
        Quaternion rotations = entity.getRenderRotations(blockState);

        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.mulPose(rotations);

        matrixStack.scale(f, f, f);
        matrixStack.scale(-1, -1, 1);

        SkinRenderContext context = SkinRenderContext.getInstance();
        context.setup(light, partialTicks1, matrixStack, buffers);
        renderer.render(mannequin, model, bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, 0, context);

        matrixStack.popPose();

        if (ModDebugger.skinnableBlock) {
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
            RenderUtils.drawBoundingBox(matrixStack, entity.getCustomRenderBoundingBox(blockState), Color.ORANGE, buffers);
            matrixStack.popPose();
        }
    }
}
