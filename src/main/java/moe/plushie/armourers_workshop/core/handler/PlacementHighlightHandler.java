package moe.plushie.armourers_workshop.core.handler;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.render.item.SkinItemStackRenderer;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModItems;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.MannequinRayTraceResult;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.SkinItemUseContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class PlacementHighlightHandler {

    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawHighlightEvent.HighlightBlock event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null || event.isCanceled()) {
            return;
        }
        // hidden hit box at inside
        // if (event.getTarget().isInside()) {
        //     BlockState state = player.level.getBlockState(event.getTarget().getBlockPos());
        //     if (state.is(ModBlocks.BOUNDING_BOX)) {
        //         event.setCanceled(true);
        //         return;
        //     }
        // }
        ItemStack itemStack = player.getMainHandItem();
        if (ModConfig.Client.enableEntityPlacementHighlight && itemStack.getItem() == ModItems.MANNEQUIN) {
            renderEntityPlacement(player, event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
        }
        if (ModConfig.Client.enableBlockPlacementHighlight && itemStack.getItem() == ModItems.SKIN) {
            SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
            if (descriptor.getType() == SkinTypes.BLOCK) {
                renderBlockPlacement(itemStack, player, event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
            }
        }
        if (ModConfig.Client.enablePaintToolPlacementHighlight && itemStack.getItem() == ModItems.BLENDING_TOOL) {
            renderPaintToolPlacement(itemStack, player, event.getTarget(), event.getInfo(), event.getMatrix(), event.getBuffers());
        }
    }

    private void renderBlockPlacement(ItemStack itemStack, PlayerEntity player, BlockRayTraceResult traceResult, ActiveRenderInfo renderInfo, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        matrixStack.pushPose();

        float f = 1 / 16.f;
        Vector3d origin = renderInfo.getPosition();
        SkinItemUseContext context = new SkinItemUseContext(player, Hand.MAIN_HAND, itemStack, traceResult);
        BlockPos location = context.getClickedPos();

        matrixStack.translate(location.getX() - origin.x(), location.getY() - origin.y(), location.getZ() - origin.z());
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.scale(f, f, f);

        for (SkinItemUseContext.Part part : context.getParts()) {
            BlockPos pos = part.getOffset();
            Color color = Color.RED;
            if (context.canPlace(part)) {
                color = Color.WHITE;
            }
            matrixStack.pushPose();
            matrixStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
            RenderUtils.drawBoundingBox(matrixStack, part.getShape(), color, buffers);
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

    private void renderEntityPlacement(PlayerEntity player, BlockRayTraceResult traceResult, ActiveRenderInfo renderInfo, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        Vector3d origin = renderInfo.getPosition();
        MannequinRayTraceResult target = MannequinRayTraceResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
        matrixStack.pushPose();

        Vector3d location = target.getLocation();

        matrixStack.translate(location.x() - origin.x(), location.y() - origin.y(), location.z() - origin.z());
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-target.getRotation()));

        BipedModel<?> model = SkinItemStackRenderer.getInstance().getMannequinModel();
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

    private void renderPaintToolPlacement(ItemStack itemStack, PlayerEntity player, BlockRayTraceResult traceResult, ActiveRenderInfo renderInfo, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        World world = player.level;
        BlockPos pos = traceResult.getBlockPos();
        Direction direction = traceResult.getDirection();
        TileEntity tileEntity = world.getBlockEntity(pos);

        // must select a paintable block to preview.
        if (!(tileEntity instanceof IPaintable)) {
            return;
        }

        int radiusSample = ToolOptions.RADIUS_SAMPLE.get(itemStack);
        int radiusEffect = ToolOptions.RADIUS_EFFECT.get(itemStack);
        boolean restrictPlane = ToolOptions.PLANE_RESTRICT.get(itemStack);

        ArrayList<BlockPos> blockSamples = BlockUtils.findTouchingBlockFaces(world, pos, direction, radiusSample, restrictPlane);
        ArrayList<BlockPos> blockEffects = BlockUtils.findTouchingBlockFaces(world, pos, direction, radiusEffect, restrictPlane);

        matrixStack.pushPose();

        Vector3d origin = renderInfo.getPosition();
        matrixStack.translate(-origin.x(), -origin.y(), -origin.z());
        matrixStack.translate(0.5f, 0.5f, 0.5f);

        Matrix4f mat = matrixStack.last().pose();
        IVertexBuilder builder = buffers.getBuffer(SkinRenderType.LINES_WITHOUT_TEST);

        for (BlockPos pos1 : blockSamples) {
            float x0 = pos1.getX() - 0.5f;
            float y0 = pos1.getY() - 0.5f;
            float z0 = pos1.getZ() - 0.5f;
            float x1 = pos1.getX() + 0.5f;
            float y1 = pos1.getY() + 0.5f;
            float z1 = pos1.getZ() + 0.5f;
            RenderUtils.drawBoundingBox(mat, x0, y0, z0, x1, y1, z1, Color.RED, builder);
        }

        for (BlockPos pos1 : blockEffects) {
            float x0 = pos1.getX() - 0.4f;
            float y0 = pos1.getY() - 0.4f;
            float z0 = pos1.getZ() - 0.4f;
            float x1 = pos1.getX() + 0.4f;
            float y1 = pos1.getY() + 0.4f;
            float z1 = pos1.getZ() + 0.4f;
            RenderUtils.drawBoundingBox(mat, x0, y0, z0, x1, y1, z1, Color.GREEN, builder);
        }

        matrixStack.popPose();
    }
}
