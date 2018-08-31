package riskyken.armourers_workshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.common.skin.data.Skin;
import riskyken.armourers_workshop.common.skin.data.SkinProperties;
import riskyken.armourers_workshop.common.tileentities.TileEntitySkinnable;

public class ContainerSkinnable extends Container {

    private final TileEntitySkinnable tileEntity;
    private int size;
    
    public ContainerSkinnable(InventoryPlayer invPlayer, TileEntitySkinnable tileEntity, Skin skin) {
        this.tileEntity = tileEntity;
        
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
        
        
        int playerInvY = height * 18 + 41;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
        
        int guiWidth = 176;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                addSlotToContainer(new Slot(inventory, x + y * width, (guiWidth / 2 - (width * 18) / 2) + 1 + 18 * x, 21 + y * 18));
            }
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.getDistanceSq(tileEntity.getPos()) <= 64;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            
            if (slotId > 35) {
                // Moving from tile entity to player.
                if (!this.mergeItemStack(stack, 9, 36, false)) {
                    if (!this.mergeItemStack(stack, 0, 9, false)) {
                        return null;
                    }
                }
            } else {
                // Moving from player to tile entity.
                if (!this.mergeItemStack(stack, 36, 36 + size, false)) {
                    return null;
                }
            }
            
            if (stack.getCount() == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(player, stack);
            
            return result;
        }
        return null;
    }
}
