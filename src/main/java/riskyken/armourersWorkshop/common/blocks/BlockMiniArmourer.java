package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockMiniArmourer extends AbstractModBlock implements ITileEntityProvider {

    public BlockMiniArmourer() {
        super(LibBlockNames.MINI_ARMOURER);
    }
    
    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    }
    
    @Override
    public IIcon getIcon(IBlockAccess p_149673_1_, int p_149673_2_,
            int p_149673_3_, int p_149673_4_, int p_149673_5_) {
        // TODO Auto-generated method stub
        return super.getIcon(p_149673_1_, p_149673_2_, p_149673_3_, p_149673_4_,
                p_149673_5_);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityMiniArmourer();
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public boolean isNormalCube() {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return -1;
    }
}
