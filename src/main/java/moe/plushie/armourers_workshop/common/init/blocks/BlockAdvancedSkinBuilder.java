package moe.plushie.armourers_workshop.common.init.blocks;

import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAdvancedSkinBuilder extends AbstractModBlockContainer {

    public BlockAdvancedSkinBuilder() {
        super(LibBlockNames.ADVANCED_SKIN_BUILDER);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // openGui(playerIn, EnumGuiId.ADVANCED_SKIN_BUILDER, worldIn, pos, state, facing);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityAdvancedSkinBuilder();
    }
}
