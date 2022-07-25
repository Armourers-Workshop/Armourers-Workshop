package moe.plushie.armourers_workshop.builder.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.ExtendedFaceRenderer;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

@SuppressWarnings({"unused"})
@Environment(value = EnvType.CLIENT)
public class SkinCubeItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static SkinCubeItemRenderer INSTANCE;

    private final RenderType normalRenderType = SkinRenderType.layeredItemSolid(RenderUtils.TEX_BLOCK_CUBE);
    private final RenderType translucentRenderType = SkinRenderType.layeredItemTranslucent(RenderUtils.TEX_BLOCK_CUBE_GLASS);
    private final RenderType unsortedTranslucentRenderType = SkinRenderType.unsortedTranslucent(RenderUtils.TEX_BLOCK_CUBE_GLASS);

    public static SkinCubeItemRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinCubeItemRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        SkinCubeItem item = (SkinCubeItem) itemStack.getItem();
        BlockPaintColor blockPaintColor = item.getItemColors(itemStack);
        if (blockPaintColor == null) {
            blockPaintColor = BlockPaintColor.WHITE;
        }
        Block block = item.getBlock();

        boolean isGlowing = block.is(ModBlocks.SKIN_CUBE_GLOWING.get()) || block.is(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());
        boolean isGlass = block.is(ModBlocks.SKIN_CUBE_GLASS.get()) || block.is(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());

        RenderType renderType = normalRenderType;
        if (isGlass) {
            renderType = translucentRenderType;
        }
        if (isGlowing) {
            float f1 = 1 / 16.0f;
            float f = 14 / 16.0f;
            VertexConsumer builder2 = renderTypeBuffer.getBuffer(renderType);
            matrixStack.pushPose();
            matrixStack.translate(f1, f1, f1);
            matrixStack.scale(f, f, f);
            renderCube(blockPaintColor, light, overlay, matrixStack, builder2);
            matrixStack.popPose();
            renderType = unsortedTranslucentRenderType;
        }
        VertexConsumer builder1 = renderTypeBuffer.getBuffer(renderType);
        renderCube(blockPaintColor, light, overlay, matrixStack, builder1);
    }

    public void renderCube(BlockPaintColor blockPaintColor, int light, int overlay, PoseStack matrixStack, VertexConsumer builder) {
        for (Direction dir : Direction.values()) {
            IPaintColor paintColor = blockPaintColor.getOrDefault(dir, PaintColor.WHITE);
            ExtendedFaceRenderer.render2(0, 0, 0, dir, paintColor, 255, light, overlay, matrixStack, builder);
        }
    }
}
