package moe.plushie.armourers_workshop.core.client.render;

import com.apple.library.uikit.UIColor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.QuadToLineVertexBuilder;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.data.MannequinHitResult;
import moe.plushie.armourers_workshop.core.data.SkinBlockPlaceContext;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class HighlightPlacementRenderer {

    public static void renderBlock(ItemStack itemStack, Player player, BlockHitResult traceResult, Camera renderInfo, PoseStack matrixStack, MultiBufferSource buffers) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.getType() != SkinTypes.BLOCK) {
            return;
        }

        matrixStack.pushPose();

        float f = 1 / 16.f;
        Vec3 origin = renderInfo.getPosition();
        SkinBlockPlaceContext context = new SkinBlockPlaceContext(player, InteractionHand.MAIN_HAND, itemStack, traceResult);
        BlockPos location = context.getClickedPos();

        matrixStack.translate(location.getX() - origin.x(), location.getY() - origin.y(), location.getZ() - origin.z());
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.scale(f, f, f);

        for (SkinBlockPlaceContext.Part part : context.getParts()) {
            BlockPos pos = part.getOffset();
            UIColor color = UIColor.RED;
            if (context.canPlace(part)) {
                color = UIColor.WHITE;
            }
            matrixStack.pushPose();
            matrixStack.translate(pos.getX() * 16f, pos.getY() * 16f, pos.getZ() * 16f);
            RenderSystem.drawBoundingBox(matrixStack, part.getShape(), color, buffers);
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

    public static void renderEntity(Player player, BlockHitResult traceResult, Camera renderInfo, PoseStack matrixStack, MultiBufferSource buffers) {
        Vec3 origin = renderInfo.getPosition();
        MannequinHitResult target = MannequinHitResult.test(player, origin, traceResult.getLocation(), traceResult.getBlockPos());
        matrixStack.pushPose();

        Vec3 location = target.getLocation();

        matrixStack.translate(location.x() - origin.x(), location.y() - origin.y(), location.z() - origin.z());
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-target.getRotation()));

        MannequinModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        if (model != null) {
            float f = target.getScale() * 0.9375f; // base scale from player model
            VertexConsumer vertexBuilder = new QuadToLineVertexBuilder(buffers.getBuffer(SkinRenderType.HIGHLIGHTED_ENTITY_LINES));
            matrixStack.pushPose();
            matrixStack.scale(f, f, f);
            matrixStack.scale(-1, -1, 1);
            matrixStack.translate(0.0f, -1.501f, 0.0f);
            model.renderToBuffer(matrixStack, vertexBuilder, 0, 0, 1, 1, 1, 1);  // m,vb,l,p,r,g,b,a
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

    public static void renderPaintTool(ItemStack itemStack, Player player, BlockHitResult traceResult, Camera renderInfo, PoseStack matrixStack, MultiBufferSource buffers) {
        Level level = player.level;
        BlockPos pos = traceResult.getBlockPos();
        Direction direction = traceResult.getDirection();
        BlockEntity tileEntity = level.getBlockEntity(pos);

        // must select a paintable block to preview.
        if (!(tileEntity instanceof IPaintable)) {
            return;
        }

        int radiusSample = ToolOptions.RADIUS_SAMPLE.get(itemStack);
        int radiusEffect = ToolOptions.RADIUS_EFFECT.get(itemStack);
        boolean restrictPlane = ToolOptions.PLANE_RESTRICT.get(itemStack);

        ArrayList<BlockPos> blockSamples = BlockUtils.findTouchingBlockFaces(level, pos, direction, radiusSample, restrictPlane);
        ArrayList<BlockPos> blockEffects = BlockUtils.findTouchingBlockFaces(level, pos, direction, radiusEffect, restrictPlane);

        matrixStack.pushPose();

        Vec3 origin = renderInfo.getPosition();
        matrixStack.translate(-origin.x(), -origin.y(), -origin.z());
        matrixStack.translate(0.5f, 0.5f, 0.5f);

        VertexConsumer builder = buffers.getBuffer(SkinRenderType.HIGHLIGHTED_LINES);

        for (BlockPos pos1 : blockSamples) {
            float x0 = pos1.getX() - 0.5f;
            float y0 = pos1.getY() - 0.5f;
            float z0 = pos1.getZ() - 0.5f;
            float x1 = pos1.getX() + 0.5f;
            float y1 = pos1.getY() + 0.5f;
            float z1 = pos1.getZ() + 0.5f;
            RenderSystem.drawBoundingBox(matrixStack, x0, y0, z0, x1, y1, z1, UIColor.RED, builder);
        }

        for (BlockPos pos1 : blockEffects) {
            float x0 = pos1.getX() - 0.4f;
            float y0 = pos1.getY() - 0.4f;
            float z0 = pos1.getZ() - 0.4f;
            float x1 = pos1.getX() + 0.4f;
            float y1 = pos1.getY() + 0.4f;
            float z1 = pos1.getZ() + 0.4f;
            RenderSystem.drawBoundingBox(matrixStack, x0, y0, z0, x1, y1, z1, UIColor.GREEN, builder);
        }

        matrixStack.popPose();
    }
}
