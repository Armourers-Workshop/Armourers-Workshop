package moe.plushie.armourers_workshop.builder.client.render;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.builder.item.SkinCubeItem;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractSpecialModelRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ShapeElement;
import moe.plushie.armourers_workshop.core.data.color.BlockPaintColor;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.init.ModBlocks;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.world.item.ItemStack;

@OnlyIn(Dist.CLIENT)
public class SkinCubeItemRenderer extends AbstractSpecialModelRenderer<ItemStack> {

    public static final IDataMapCodec<SkinCubeItemRenderer> MAP_CODEC = IDataMapCodec.unit(SkinCubeItemRenderer::new);

    private final OpenRectangle3f outer = new OpenRectangle3f(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    private final OpenRectangle3f inner = new OpenRectangle3f(0.0625f, 0.0625f, 0.0625f, 0.875f, 0.875f, 0.875f);

    @Override
    protected void abi$render(ItemStack itemStack, OpenItemDisplayContext itemDisplayContext, int lightmap, int overlay, IGraphicsContext context) {
        if (itemStack.isEmpty()) {
            return;
        }
        var item = (SkinCubeItem) itemStack.getItem();
        var blockPaintColor = item.getItemColors(itemStack);
        if (blockPaintColor == null) {
            blockPaintColor = BlockPaintColor.WHITE;
        }
        var block = item.getBlock();

        var isGlowing = block.equals(ModBlocks.SKIN_CUBE_GLOWING.get()) || block.equals(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());
        var isGlass = block.equals(ModBlocks.SKIN_CUBE_GLASS.get()) || block.equals(ModBlocks.SKIN_CUBE_GLASS_GLOWING.get());


        var outerRenderType = SkinRenderType.BLOCK_CUBE;
        var innerRenderType = SkinRenderType.BLOCK_CUBE;

        if (isGlass) {
            outerRenderType = SkinRenderType.BLOCK_CUBE_GLASS;
            innerRenderType = SkinRenderType.BLOCK_CUBE_GLASS;
        }
        if (isGlowing) {
            outerRenderType = SkinRenderType.BLOCK_CUBE_GLASS_UNSORTED;
        }

        if (innerRenderType != outerRenderType) {
            context.draw(ShapeElement.fill(inner, lightmap, overlay, blockPaintColor, innerRenderType));
        }
        context.draw(ShapeElement.fill(outer, lightmap, overlay, blockPaintColor, outerRenderType));

        if (ModDebugger.cubeItem) {
            context.draw(ShapeElement.stroke(outer, Colors.ORANGE));
            if (innerRenderType != outerRenderType) {
                context.draw(ShapeElement.stroke(inner, Colors.ORANGE));
            }
        }
    }

    @Override
    protected ItemStack abi$extractArgument(ItemStack itemStack) {
        return itemStack;
    }
}
