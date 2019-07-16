package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class ModTileContainer<TILETYPE extends TileEntity> extends ModContainer {

    protected final TILETYPE tileEntity;

    public ModTileContainer(EntityPlayer player, TILETYPE tileEntity) {
        super(player);
        this.tileEntity = tileEntity;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isDead & playerIn.getDistanceSq(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) <= 64;
    }

    public TILETYPE getTileEntity() {
        return tileEntity;
    }
}
