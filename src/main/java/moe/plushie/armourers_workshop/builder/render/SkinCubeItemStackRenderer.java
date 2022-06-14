package moe.plushie.armourers_workshop.builder.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;

@SuppressWarnings({"NullableProblems", "unused"})
@OnlyIn(Dist.CLIENT)
public class SkinCubeItemStackRenderer extends ItemStackTileEntityRenderer {

    private static SkinCubeItemStackRenderer INSTANCE;

    private final ModelRenderer box = new ModelRenderer(16, 16, 0, 0);
    private final ModelRenderer insideBox = new ModelRenderer(16, 16, 0, 0);

    private final RenderType normalRenderType = ForgeRenderTypes.getItemLayeredSolid(RenderUtils.TEX_BLOCK_CUBE);
    private final RenderType translucentRenderType = ForgeRenderTypes.getItemLayeredTranslucent(RenderUtils.TEX_BLOCK_CUBE_GLASS);
    private final RenderType unsortedTranslucentRenderType = ForgeRenderTypes.getUnsortedTranslucent(RenderUtils.TEX_BLOCK_CUBE_GLASS);

    public static SkinCubeItemStackRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinCubeItemStackRenderer();
            INSTANCE.box.addBox(0, 0, 0, 16, 16, 16);
            INSTANCE.insideBox.addBox(1, 1, 1, 14, 14, 14);
        }
        return INSTANCE;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int light, int overlay) {
        if (itemStack.isEmpty()) {
            return;
        }
        SkinCubeItem item = (SkinCubeItem) itemStack.getItem();
        Block block = item.getBlock();
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor == null) {
            paintColor = PaintColor.WHITE;
        }
        int color = paintColor.getRGB();
        float red = ((color >> 16) & 0xff) / 255f;
        float green = ((color >> 8) & 0xff) / 255f;
        float blue = (color & 0xff) / 255f;

        boolean isGlowing = block.is(ModBlocks.SKIN_CUBE_GLOWING) || block.is(ModBlocks.SKIN_CUBE_GLASS_GLOWING);
        boolean isGlass = block.is(ModBlocks.SKIN_CUBE_GLASS) || block.is(ModBlocks.SKIN_CUBE_GLASS_GLOWING);

        RenderType renderType = normalRenderType;
        if (isGlass) {
            renderType = translucentRenderType;
        }

        if (isGlowing) {
            IVertexBuilder builder2 = renderTypeBuffer.getBuffer(renderType);
            insideBox.render(matrixStack, builder2, light, overlay, red, green, blue, 1);
            renderType = unsortedTranslucentRenderType;
        }

        IVertexBuilder builder1 = renderTypeBuffer.getBuffer(renderType);
        box.render(matrixStack, builder1, light, overlay, red, green, blue, 1);
    }
}
