package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityDyeTable;

public class BlockDyeTable extends AbstractModBlockContainer {

    public BlockDyeTable() {
        super(LibBlockNames.DYE_TABLE);
        setSortPriority(150);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.DYE_TABLE, world, x, y, z);
        }
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityDyeTable();
    }
}
