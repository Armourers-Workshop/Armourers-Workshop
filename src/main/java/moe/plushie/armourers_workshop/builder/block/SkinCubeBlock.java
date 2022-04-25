package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.api.client.render.IHasCustomizeRenderType;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.utils.OptionalDirection;
import moe.plushie.armourers_workshop.core.utils.OptionalDirectionProperty;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class SkinCubeBlock extends Block implements IHasCustomizeRenderType {

    public static final OptionalDirectionProperty MARKER = OptionalDirectionProperty.create("marker", OptionalDirection.values());

    public SkinCubeBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(MARKER, OptionalDirection.NONE));
    }

    @OnlyIn(Dist.CLIENT)
    public static IBlockColor getColorProvider() {  // by ColorHandlerEvent.Block.register(ColourMixerBlock.getColorProvider(1), ...)
        return (state, world, pos, tintIndex1) -> {
            if (world == null || pos == null) {
                return 0xffffffff;
            }
            switch (tintIndex1) {
                case 0:
                    return 0xffff0000;
                case 1:
                    return 0xff00ff00;
                case 2:
                    return 0xff0000ff;
                case 3:
                    return 0xffff00ff;
                case 4:
                    return 0xff00ffff;
                case 5:
                    return 0xffffff00;
            }
//            TileEntity entity = world.getBlockEntity(pos);
//            if (entity instanceof ColourMixerTileEntity) {
//                return ((ColourMixerTileEntity) entity).getColor().getRGB() | 0xff000000;
//            }
            return 0xffffffff;
        };
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState state1, Direction dir) {
        // the same block can be omitted
        return state.getBlock() == state1.getBlock();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MARKER);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable IBlockReader world, List<ITextComponent> tooltips, ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, tooltips, flags);
        tooltips.addAll(TranslateUtils.subtitles(getDescriptionId() + ".flavour"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public RenderType getItemRenderType(boolean flags) {
        //return flags ? Atlases.translucentCullBlockSheet() : Atlases.translucentItemSheet();
        return SkinRenderType.ITEM_TRANSLUCENT_WITHOUT_SORTED;
    }
}
