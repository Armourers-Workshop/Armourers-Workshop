package riskyken.armourersWorkshop.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;
import riskyken.armourersWorkshop.utils.BlockUtils;

public class BlockHologramProjector extends AbstractModBlockContainer {

    public BlockHologramProjector() {
        super(LibBlockNames.HOLOGRAM_PROJECTOR);
        setSortPriority(150);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        BlockUtils.dropInventoryBlocks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack) {
        int dir = BlockUtils.determineOrientation(x, y, z, entityLivingBase);
        world.setBlockMetadataWithNotify(x, y, z, dir, 2);
    }
    
    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int p_149714_5_) {
        updatePoweredState(world, x, y, z);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityHologramProjector();
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.HOLOGRAM_PROJECTOR, world, x, y, z);
        }
        return true;
    }
    
    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        world.setBlockMetadataWithNotify(x, y, z, axis.ordinal(), 2);
        return true;
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        updatePoweredState(world, x, y, z);
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        updatePoweredState(world, x, y, z);
    }
    
    private void updatePoweredState(World world, int x, int y, int z) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null && tileEntity instanceof TileEntityHologramProjector) {
                ((TileEntityHologramProjector)tileEntity).updatePoweredState();
            }
        }
    }
}
