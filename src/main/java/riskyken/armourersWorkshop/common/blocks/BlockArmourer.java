package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
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
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public class BlockArmourer extends AbstractModBlockContainer {

    public BlockArmourer() {
        super(LibBlockNames.ARMOURER);
    }
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)placer;
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null && te instanceof TileEntityArmourer) {
                EnumFacing direction = EnumFacing.VALUES[(UtilBlocks.determineOrientationSide(worldIn, pos, placer))];
                ((TileEntityArmourer)te).setDirection(EnumFacing.NORTH);
                if (!worldIn.isRemote) {
                    ((TileEntityArmourer)te).setGameProfile(player.getGameProfile());
                    ((TileEntityArmourer)te).onPlaced();
                }
            }
        }
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        UtilBlocks.dropInventoryBlocks(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null & te instanceof TileEntityArmourer) {
            ((TileEntityArmourer)te).preRemove();
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
            EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!playerIn.canPlayerEdit(pos, side, heldItem)) {
            return false;
        }
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance, LibGuiIds.ARMOURER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityArmourer();
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
