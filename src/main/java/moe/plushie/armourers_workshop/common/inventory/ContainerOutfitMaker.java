package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityOutfitMaker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerOutfitMaker extends ModTileContainer<TileEntityOutfitMaker> implements IButtonPress {

    public ContainerOutfitMaker(InventoryPlayer invPlayer, TileEntityOutfitMaker tileEntity) {
        super(invPlayer, tileEntity);
        addPlayerSlots(8, 142);
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        // TODO Auto-generated method stub
        return super.transferStackFromPlayer(playerIn, index);
    }
    
    @Override
    public void buttonPressed(byte buttonId) {
        // TODO Auto-generated method stub
    }
}
