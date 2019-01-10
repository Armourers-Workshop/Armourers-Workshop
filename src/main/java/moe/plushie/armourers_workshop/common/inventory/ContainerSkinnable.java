package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSkinnable extends ModTileContainer<TileEntitySkinnable> {

    private int size;
    
    public ContainerSkinnable(InventoryPlayer invPlayer, TileEntitySkinnable tileEntity, Skin skin) {
        super(invPlayer, tileEntity);
        
        
        boolean ender = SkinProperties.PROP_BLOCK_ENDER_INVENTORY.getValue(skin.getProperties());
        int width = SkinProperties.PROP_BLOCK_INVENTORY_WIDTH.getValue(skin.getProperties());
        int height = SkinProperties.PROP_BLOCK_INVENTORY_HEIGHT.getValue(skin.getProperties());
        
        IInventory inventory = tileEntity.getInventory();
        if (ender) {
            width = 9;
            height = 3;
            inventory = invPlayer.player.getInventoryEnderChest();
        }
        
        size = width * height;
        
        addPlayerSlots(8, height * 18 + 41);
        
        int guiWidth = 176;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                addSlotToContainer(new Slot(inventory, x + y * width, (guiWidth / 2 - (width * 18) / 2) + 1 + 18 * x, 21 + y * 18));
            }
        }
    }
    
    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            // Moving from tile entity to player.
            if (!this.mergeItemStack(stack, getPlayerInvEndIndex(), getPlayerInvEndIndex() + size, false)) {
                return ItemStack.EMPTY;
            }
            
            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(playerIn, stack);
            
            return result;
        }
        return ItemStack.EMPTY;
    }
}
