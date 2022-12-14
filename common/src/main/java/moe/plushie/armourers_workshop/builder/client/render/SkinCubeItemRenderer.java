package moe.plushie.armourers_workshop.builder.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.compatibility.AbstractItemEntityRenderer;
import moe.plushie.armourers_workshop.compatibility.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.ExtendedFaceRenderer;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

@Environment(value = EnvType.CLIENT)
public class SkinCubeItemRenderer extends AbstractItemEntityRenderer {

    private static SkinCubeItemRenderer INSTANCE;

    public static SkinCubeItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinCubeItemRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStackIn, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        SkinCubeItem item = (SkinCubeItem) itemStack.getItem();
        BlockPaintColor blockPaintColor = item.getItemColors(itemStack);
        if (blockPaintColor == null) {
            blockPaintColor = BlockPaintColor.WHITE;
        }
        IPoseStack poseStack = AbstractPoseStack.wrap(poseStackIn);
        Block block = item.getBlock();

        boolean isGlowing = block.equals(ModBlocks.SKIN_CUBE_GLOWING.get()) || block.equals(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());
        boolean isGlass = block.equals(ModBlocks.SKIN_CUBE_GLASS.get()) || block.equals(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());

        RenderType renderType = SkinRenderType.BLOCK_CUBE;
        if (isGlass) {
            renderType = SkinRenderType.BLOCK_CUBE_GLASS;
        }
        if (isGlowing) {
            float f1 = 1 / 16.0f;
            float f = 14 / 16.0f;
            VertexConsumer builder2 = renderTypeBuffer.getBuffer(renderType);
            poseStack.pushPose();
            poseStack.translate(f1, f1, f1);
            poseStack.scale(f, f, f);
            renderCube(blockPaintColor, light, overlay, poseStack, builder2);
            poseStack.popPose();
            renderType = SkinRenderType.BLOCK_CUBE_GLASS_UNSORTED;
        }
        VertexConsumer builder1 = renderTypeBuffer.getBuffer(renderType);
        renderCube(blockPaintColor, light, overlay, poseStack, builder1);
    }

    public void renderCube(BlockPaintColor blockPaintColor, int light, int overlay, IPoseStack poseStack, VertexConsumer builder) {
        for (Direction dir : Direction.values()) {
            IPaintColor paintColor = blockPaintColor.getOrDefault(dir, PaintColor.WHITE);
            ExtendedFaceRenderer.render2(0, 0, 0, dir, paintColor, 255, light, overlay, poseStack, builder);
        }
    }
}
