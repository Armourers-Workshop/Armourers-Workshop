package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;

public class BlockMiniArmourer extends AbstractModBlockContainer {

    public BlockMiniArmourer() {
        super(LibBlockNames.MINI_ARMOURER);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
            EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!playerIn.canPlayerEdit(pos, side, heldItem)) {
            return false;
        }
        if (!worldIn.isRemote) {
            if (!playerIn.isSneaking()) {
                FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance, LibGuiIds.MINI_ARMOURER, worldIn, pos.getX(), pos.getY(), pos.getZ());
            } else {
                FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance, LibGuiIds.MINI_ARMOURER_BUILDING, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }
    
    @Override
    public Block setUnlocalizedName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setUnlocalizedName(name);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityMiniArmourer();
    }
}
