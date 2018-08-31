package riskyken.armourers_workshop.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourers_workshop.common.lib.LibBlockNames;
import riskyken.armourers_workshop.common.tileentities.TileEntityMiniArmourer;

public class BlockMiniArmourer extends AbstractModBlockContainer {

    public BlockMiniArmourer() {
        super(LibBlockNames.MINI_ARMOURER);
        setSortPriority(-1);
    }
    /*
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            if (!player.isSneaking()) {
                FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.MINI_ARMOURER, world, x, y, z);
            } else {
                FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.MINI_ARMOURER_BUILDING, world, x, y, z);
            }
        }
        return true;
    }*/

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityMiniArmourer();
    }
}
