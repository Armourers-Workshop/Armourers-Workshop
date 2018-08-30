package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourable;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

public class BlockColourable extends AbstractModBlockContainer implements IPantableBlock {
    
    public BlockColourable(String name, boolean glowing) {
        super(name);
        if (glowing) {
            setLightLevel(1.0F);
        }
        setHardness(1.0F);
        setLightOpacity(0);
        setSortPriority(123);
        if (glowing) {
            setSortPriority(122);
        }
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.DYE) {
            if (world.isRemote) { return true; }
            this.setColour(world, x, y, z, UtilColour.getMinecraftColor(-player.getCurrentEquippedItem().getItemDamage() + 15, ColourFamily.MINECRAFT), side);
            return true;
        }
        return false;
    }
    
    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityColourable();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean setColour(IBlockAccess world, int x, int y, int z, int colour, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IPantable) {
            ((IPantable)te).setColour(colour, side);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean setColour(IBlockAccess world, int x, int y, int z, byte[] rgb, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IPantable) {
            ((IPantable)te).setColour(rgb, side);
            return true;
        }
        return false;
    }

    @Override
    public int getColour(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getColour(side);
        }
        return 0;
    }
    
    @Override
    public ICubeColour getColour(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getColour();
        }
        return new CubeColour();
    }
    
    @Override
    public void setPaintType(IBlockAccess world, int x, int y, int z, PaintType paintType, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IPantable) {
            ((IPantable)te).setPaintType(paintType, side);
        }
    }
    
    @Override
    public PaintType getPaintType(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof IPantable) {
            return ((IPantable)te).getPaintType(side);
        }
        return PaintType.NORMAL;
    }
    
    @Override
    public boolean isRemoteOnly(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return ArmourersWorkshop.proxy.getBlockRenderType(this);
    }
}
