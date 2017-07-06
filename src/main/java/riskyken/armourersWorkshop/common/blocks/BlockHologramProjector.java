package riskyken.armourersWorkshop.common.blocks;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;
import riskyken.armourersWorkshop.utils.BlockUtils;

public class BlockHologramProjector extends AbstractModBlockContainer {

    public BlockHologramProjector() {
        super(LibBlockNames.HOLOGRAM_PROJECTOR);
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
    
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.HOLOGRAM_PROJECTOR_SIDE);
        iconTop = register.registerIcon(LibBlockResources.HOLOGRAM_PROJECTOR_TOP);
        iconBottom = register.registerIcon(LibBlockResources.HOLOGRAM_PROJECTOR_BOTTOM);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 1) {
            return iconTop;
        }
        if (side == 0) {
            return iconBottom;
        }
        return blockIcon;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta < 0 | meta > 5) {
            return blockIcon;
        }
        ForgeDirection dir = ForgeDirection.values()[meta];
        if (side == meta) {
            return iconTop;
        }
        if (side == dir.getOpposite().ordinal()) {
            return iconBottom;
        }
        
        return blockIcon;
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
}
