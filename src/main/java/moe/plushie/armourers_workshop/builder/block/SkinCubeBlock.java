package moe.plushie.armourers_workshop.builder.block;

import moe.plushie.armourers_workshop.api.client.render.IHasCustomizeRenderType;
import moe.plushie.armourers_workshop.api.common.IBlockTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.builder.tileentity.SkinCubeTileEntity;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.utils.OptionalDirection;
import moe.plushie.armourers_workshop.core.utils.OptionalDirectionProperty;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class SkinCubeBlock extends Block implements IBlockTintColorProvider, IHasCustomizeRenderType {

    public static final OptionalDirectionProperty MARKER = OptionalDirectionProperty.create("marker", OptionalDirection.values());

    public SkinCubeBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(MARKER, OptionalDirection.NONE));
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState state1, Direction dir) {
        // the same block can be omitted
        return state.getBlock() == state1.getBlock();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SkinCubeTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MARKER);
    }

    @Override
    public int getTintColor(BlockState blockState, @Nullable IBlockDisplayReader reader, @Nullable BlockPos blockPos, int index) {
        if (reader == null || blockPos == null) {
            return 0xffffffff;
        }
        Direction direction = Direction.NORTH;
        if (index > 0 && index < 7) {
            direction = Direction.values()[index - 1];
        }
        TileEntity tileEntity = reader.getBlockEntity(blockPos);
        if (tileEntity instanceof SkinCubeTileEntity) {
            IPaintColor paintColor = ((SkinCubeTileEntity) tileEntity).getColor(direction);
            return paintColor.getRGB() | 0xff000000;
        }
        return 0xffffffff;
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
