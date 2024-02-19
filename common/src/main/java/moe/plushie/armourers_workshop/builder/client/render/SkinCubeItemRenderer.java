package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractItemStackRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.ExtendedFaceRenderer;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class SkinCubeItemRenderer extends AbstractItemStackRenderer {

    private static SkinCubeItemRenderer INSTANCE;

    public static SkinCubeItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinCubeItemRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, AbstractItemTransformType transformType, IPoseStack poseStack, IBufferSource bufferSource, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        auto item = (SkinCubeItem) itemStack.getItem();
        auto blockPaintColor = item.getItemColors(itemStack);
        if (blockPaintColor == null) {
            blockPaintColor = BlockPaintColor.WHITE;
        }
        auto block = item.getBlock();

        boolean isGlowing = block.equals(ModBlocks.SKIN_CUBE_GLOWING.get()) || block.equals(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());
        boolean isGlass = block.equals(ModBlocks.SKIN_CUBE_GLASS.get()) || block.equals(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());

        auto renderType = SkinRenderType.BLOCK_CUBE;
        if (isGlass) {
            renderType = SkinRenderType.BLOCK_CUBE_GLASS;
        }
        if (isGlowing) {
            float f1 = 1 / 16.0f;
            float f = 14 / 16.0f;
            auto builder2 = bufferSource.getBuffer(renderType);
            poseStack.pushPose();
            poseStack.translate(f1, f1, f1);
            poseStack.scale(f, f, f);
            renderCube(blockPaintColor, light, overlay, poseStack, builder2);
            poseStack.popPose();
            renderType = SkinRenderType.BLOCK_CUBE_GLASS_UNSORTED;
        }
        auto builder1 = bufferSource.getBuffer(renderType);
        renderCube(blockPaintColor, light, overlay, poseStack, builder1);
    }

    public void renderCube(BlockPaintColor blockPaintColor, int light, int overlay, IPoseStack poseStack, IVertexConsumer builder) {
        for (auto dir : Direction.values()) {
            auto paintColor = blockPaintColor.getOrDefault(dir, PaintColor.WHITE);
            ExtendedFaceRenderer.render2(0, 0, 0, dir, paintColor, 255, light, overlay, poseStack, builder);
        }
    }
}
