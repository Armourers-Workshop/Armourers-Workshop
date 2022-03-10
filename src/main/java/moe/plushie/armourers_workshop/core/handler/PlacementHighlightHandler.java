package moe.plushie.armourers_workshop.core.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.MannequinRayTraceResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

    private void renderBlockPlacement(PlayerEntity player, BlockRayTraceResult traceResult, ActiveRenderInfo renderInfo, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
    }

    private void renderEntityPlacement(PlayerEntity player, BlockRayTraceResult traceResult, ActiveRenderInfo renderInfo, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        Vector3d origin = renderInfo.getPosition();
        MannequinRayTraceResult target = MannequinRayTraceResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
        matrixStack.pushPose();

        Vector3d location = target.getLocation();

        matrixStack.translate(location.x() - origin.x(), location.y() - origin.y(), location.z() - origin.z());
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-target.getRotation()));

//        RenderUtils.drawPoint(matrixStack, renderTypeBuffer);

        BipedModel<?> model = SkinItemRenderer.getItemStackRenderer().getMannequinModel();
        if (model != null) {
            float f = target.getScale() * 0.9375f; // base scale from player model
            IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(SkinRenderType.ENTITY_OUTLINE);
            matrixStack.pushPose();
            matrixStack.scale(f, f, f);
            matrixStack.scale(-1, -1, 1);
            matrixStack.translate(0.0f, -1.501f, 0.0f);
            model.renderToBuffer(matrixStack, vertexBuilder, 0, 0, 0, 0, 0, 1);  // m,vb,l,p,r,g,b,a
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

}
