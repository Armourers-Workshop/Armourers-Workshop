package moe.plushie.armourers_workshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public abstract class ModTileContainer<TILETYPE extends TileEntity> extends ModContainer {

    protected final TILETYPE tileEntity;
    
    public ModTileContainer(InventoryPlayer invPlayer, TILETYPE tileEntity) {
        super(invPlayer);
        this.tileEntity = tileEntity;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !playerIn.isDead & playerIn.getDistanceSq(tileEntity.getPos()) <= 64;
    }
    
    public TILETYPE getTileEntity() {
        return tileEntity;
    }
}
