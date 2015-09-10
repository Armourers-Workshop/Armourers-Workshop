package riskyken.armourersWorkshop.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.IBlockAccess;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;

public class BlockColourableGlass extends BlockColourable {

    public BlockColourableGlass(String name, boolean glowing) {
        super(name, true);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.COLOURABLE_GLASS);
        markerOverlay = register.registerIcon(LibBlockResources.MARKER);
        noTexture = register.registerIcon(LibBlockResources.NO_TEXTURE);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        Block sideBlock = world.getBlock(x, y, z);
        if (sideBlock == this) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
