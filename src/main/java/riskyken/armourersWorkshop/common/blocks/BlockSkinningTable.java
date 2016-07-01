package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinningTable;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public class BlockSkinningTable extends AbstractModBlockContainer {

    public BlockSkinningTable() {
        super(LibBlockNames.SKINNING_TABLE);
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null && te instanceof TileEntitySkinningTable) {
            TileEntitySkinningTable tileEntity = (TileEntitySkinningTable) te;
            UtilBlocks.dropInventoryBlocks(worldIn, tileEntity.getCraftingInventory(), pos);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
            EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance, LibGuiIds.SKNNING_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinningTable();
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
