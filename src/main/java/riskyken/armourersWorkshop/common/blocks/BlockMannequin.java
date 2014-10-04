package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockMannequin extends AbstractModBlock implements ITileEntityProvider {

    public BlockMannequin() {
        super(LibBlockNames.MANNEQUIN);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ModItems.equipmentSkin) {
            if (world.isRemote) { return true; }
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityMannequin) {
                ((TileEntityMannequin)te).setEquipment(player.getCurrentEquippedItem());
            }
            return true;
        }
        return false;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityMannequin();
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
