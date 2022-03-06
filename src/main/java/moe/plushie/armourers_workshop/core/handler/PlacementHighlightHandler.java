package moe.plushie.armourers_workshop.core.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderType;
import moe.plushie.armourers_workshop.core.utils.MannequinRayTraceResult;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.TrigUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PlacementHighlightHandler {


    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawHighlightEvent.HighlightBlock event) {
        drawBounds(event);
    }

    private void drawBounds(DrawHighlightEvent.HighlightBlock event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Vector3d origin = event.getInfo().getPosition();
        BlockRayTraceResult traceResult = event.getTarget();
        MannequinRayTraceResult target = MannequinRayTraceResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
        if (target == null || event.isCanceled()) {
            return;
        }
        IRenderTypeBuffer renderTypeBuffer = event.getBuffers();
        MatrixStack matrixStack = event.getMatrix();
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
