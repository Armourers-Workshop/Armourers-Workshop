package riskyken.armourersWorkshop.common.blocks;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;

public class BlockGlobalSkinLibrary extends AbstractModBlock implements ITileEntityProvider {

    public BlockGlobalSkinLibrary() {
        super(LibBlockNames.GLOBAL_SKIN_LIBRARY);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.GLOBAL_SKIN_LIBRARY, world, x, y, z);
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.GLOBAL_SKIN_LIBRARY_TOP);
        sideIcon = register.registerIcon(LibBlockResources.GLOBAL_SKIN_LIBRARY_SIDE);
        bottomIcon = register.registerIcon(LibBlockResources.GLOBAL_SKIN_LIBRARY_BOTTOM);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 1) {
            return blockIcon;
        }
        if (side == 0) {
            return bottomIcon;
        }
        if (side > 1) {
            return sideIcon;
        }
        return null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityGlobalSkinLibrary();
    }
}
