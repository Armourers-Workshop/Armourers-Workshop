package moe.plushie.armourers_workshop.builder.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.core.render.other.SkinCubeFaceRenderer;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.color.BlockPaintColor;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;

@SuppressWarnings({"NullableProblems", "unused"})
@OnlyIn(Dist.CLIENT)
public class SkinCubeItemStackRenderer extends ItemStackTileEntityRenderer {

    private static SkinCubeItemStackRenderer INSTANCE;

    private final RenderType normalRenderType = ForgeRenderTypes.getItemLayeredSolid(RenderUtils.TEX_BLOCK_CUBE);
    private final RenderType translucentRenderType = ForgeRenderTypes.getItemLayeredTranslucent(RenderUtils.TEX_BLOCK_CUBE_GLASS);
    private final RenderType unsortedTranslucentRenderType = ForgeRenderTypes.getUnsortedTranslucent(RenderUtils.TEX_BLOCK_CUBE_GLASS);

    public static SkinCubeItemStackRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinCubeItemStackRenderer();
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        SkinCubeItem item = (SkinCubeItem) itemStack.getItem();
        BlockPaintColor blockPaintColor = item.getItemColors(itemStack);
        if (blockPaintColor == null) {
            blockPaintColor = BlockPaintColor.WHITE;
        }
        Block block = item.getBlock();

        boolean isGlowing = block.is(ModBlocks.SKIN_CUBE_GLOWING) || block.is(ModBlocks.SKIN_CUBE_GLASS_GLOWING);
        boolean isGlass = block.is(ModBlocks.SKIN_CUBE_GLASS) || block.is(ModBlocks.SKIN_CUBE_GLASS_GLOWING);

        RenderType renderType = normalRenderType;
        if (isGlass) {
            renderType = translucentRenderType;
        }
        if (isGlowing) {
            float f1 = 1 / 16.0f;
            float f = 14 / 16.0f;
            IVertexBuilder builder2 = renderTypeBuffer.getBuffer(renderType);
            matrixStack.pushPose();
            matrixStack.translate(f1, f1, f1);
            matrixStack.scale(f, f, f);
            renderCube(blockPaintColor, light, overlay, matrixStack, builder2);
            matrixStack.popPose();
            renderType = unsortedTranslucentRenderType;
        }
        IVertexBuilder builder1 = renderTypeBuffer.getBuffer(renderType);
        renderCube(blockPaintColor, light, overlay, matrixStack, builder1);
    }

    public void renderCube(BlockPaintColor blockPaintColor, int light, int overlay, MatrixStack matrixStack, IVertexBuilder builder) {
        for (BlockPaintColor.Side side : BlockPaintColor.Side.values()) {
            Direction direction = side.getDirection();
            IPaintColor paintColor = blockPaintColor.getOrDefault(side, PaintColor.WHITE);
            SkinCubeFaceRenderer.render2(0, 0, 0, direction, paintColor, 255, light, overlay, matrixStack, builder);
        }
    }
}
