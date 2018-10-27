package moe.plushie.armourers_workshop.common.blocks;

import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

public class BlockAdvancedSkinBuilder extends AbstractModBlockContainer {

    public BlockAdvancedSkinBuilder() {
        super(LibBlockNames.ADVANCED_SKIN_BUILDER);
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }
}
