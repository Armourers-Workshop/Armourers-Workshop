package moe.plushie.armourers_workshop.common.blocks;

import moe.plushie.armourers_workshop.common.lib.LibBlockNames;

public class BlockMiniArmourer extends AbstractModBlock {

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
}
