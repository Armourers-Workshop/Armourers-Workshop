package riskyken.armourersWorkshop.common.blocks;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.block.ItemBlockMannequin;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityDyeTable;

public class BlockDyeTable extends AbstractModBlockContainer {

    public BlockDyeTable() {
        super(LibBlockNames.DYE_TABLE);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.DYE_TABLE, world, x, y, z);
        }
        return true;
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ItemBlockMannequin.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon iconBottom;
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.DYE_TABLE_SIDE);
        iconTop = register.registerIcon(LibBlockResources.DYE_TABLE_TOP);
        iconBottom = register.registerIcon(LibBlockResources.DYE_TABLE_BOTTOM);
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
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityDyeTable();
    }
}
