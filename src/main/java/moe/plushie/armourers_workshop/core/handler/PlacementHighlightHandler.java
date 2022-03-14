package moe.plushie.armourers_workshop.core.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.MannequinRayTraceResult;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinItemUseContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class PlacementHighlightHandler {


    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawHighlightEvent.HighlightBlock event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || event.isCanceled()) {
            return;
        }
        ItemStack itemStack = player.getMainHandItem();
        if (AWConfig.enableEntityPlacementHighlight && itemStack.getItem() == AWItems.MANNEQUIN) {
            renderEntityPlacement(player, event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
        }
        if (AWConfig.enableBlockPlacementHighlight && itemStack.getItem() == AWItems.SKIN) {
            SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
            if (descriptor.getType() == SkinTypes.BLOCK) {
                renderBlockPlacement(player, event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
            }
        }
    }

    private void renderBlockPlacement(PlayerEntity player, BlockRayTraceResult traceResult, ActiveRenderInfo renderInfo, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
//        Vector3d origin = renderInfo.getPosition();
////        MannequinRayTraceResult target = MannequinRayTraceResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
//        matrixStack.pushPose();
//
//
////        SkinItemUseContext context = new SkinItemUseContext(player, Hand.MAIN_HAND, player.getMainHandItem(), traceResult, true);
//
//        BlockPos location = traceResult.getBlockPos();// context.getClickedPos();
////        Vector3d location = traceResult.getLocation();
//
//        matrixStack.translate(location.getX() - origin.x(), location.getY() - origin.y(), location.getZ() - origin.z());
////        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-target.getRotation()));
//
//        float f = 1 / 16.f;
//        BakedSkin bakedSkin = BakedSkin.of(player.getMainHandItem());
//        if (bakedSkin != null) {
//                        matrixStack.pushPose();
////            matrixStack.translate(0.5f, 0.5f, 0.5f);
//            matrixStack.scale(f, f, f);
////            matrixStack.mulPose(rotations);
////            matrixStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
//            RenderUtils.drawBoundingBox(matrixStack, bakedSkin.getRenderBounds(null, null, null), Color.RED, buffers);
//            matrixStack.popPose();
//
//        }
//
////        float f = 1 / 16f;
////        Quaternion rotations = context.getRotations();
////        context.getParts().forEach(p -> {
////            BlockPos pos = p.getOffset();
////            Rectangle3i rect = p.getShape();
////            matrixStack.pushPose();
////            matrixStack.translate(0.5f, 0.5f, 0.5f);
////            matrixStack.scale(f, f, f);
////            matrixStack.mulPose(rotations);
////            matrixStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
////            RenderUtils.drawBoundingBox(matrixStack, rect, Color.RED, buffers);
////            matrixStack.popPose();
////        });
//
//        RenderUtils.drawPoint(matrixStack, buffers);
//
////        BipedModel<?> model = SkinItemRenderer.getItemStackRenderer().getMannequinModel();
////        if (model != null) {
////            float f = target.getScale() * 0.9375f; // base scale from player model
////            IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(SkinRenderType.ENTITY_OUTLINE);
////            matrixStack.pushPose();
////            matrixStack.scale(f, f, f);
////            matrixStack.scale(-1, -1, 1);
////            matrixStack.translate(0.0f, -1.501f, 0.0f);
////            model.renderToBuffer(matrixStack, vertexBuilder, 0, 0, 1, 1, 1, 1);  // m,vb,l,p,r,g,b,a
////            matrixStack.popPose();
////        }
//
//        matrixStack.popPose();

    }

    private void renderEntityPlacement(PlayerEntity player, BlockRayTraceResult traceResult, ActiveRenderInfo renderInfo, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        Vector3d origin = renderInfo.getPosition();
        MannequinRayTraceResult target = MannequinRayTraceResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
        matrixStack.pushPose();

        Vector3d location = target.getLocation();

        matrixStack.translate(location.x() - origin.x(), location.y() - origin.y(), location.z() - origin.z());
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-target.getRotation()));

//        RenderUtils.drawPoint(matrixStack, buffers);

        BipedModel<?> model = SkinItemRenderer.getItemStackRenderer().getMannequinModel();
        if (model != null) {
            float f = target.getScale() * 0.9375f; // base scale from player model
            IVertexBuilder vertexBuilder = buffers.getBuffer(SkinRenderType.ENTITY_OUTLINE);
            matrixStack.pushPose();
            matrixStack.scale(f, f, f);
            matrixStack.scale(-1, -1, 1);
            matrixStack.translate(0.0f, -1.501f, 0.0f);
            model.renderToBuffer(matrixStack, vertexBuilder, 0, 0, 1, 1, 1, 1);  // m,vb,l,p,r,g,b,a
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

}
