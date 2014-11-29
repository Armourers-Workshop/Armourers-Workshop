package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

public class ContainerMannequin extends Container {

    private TileEntityMannequin tileEntity;
    
    public ContainerMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        this.tileEntity = tileEntity;
        
        for (int y = 0; y < 6; y++) {
            addSlotToContainer(new SlotEquipmentSkin(EnumEquipmentType.getOrdinal(y + 1) ,tileEntity, y, 11, 25 + 19 * y));
        }
        
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 232));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 174 + y * 18));
            }
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotId < 6) {
                if (!this.mergeItemStack(stack, 15, 42, false)) {
                    if (!this.mergeItemStack(stack, 6, 15, false)) {
                        return null;
                    }
                }
            } else {
                if (stack.getItem() instanceof ItemEquipmentSkin) {
                    switch (stack.getItemDamage()) {
                    case 0:
                        if (!this.mergeItemStack(stack, 0, 1, false)) {
                            return null;
                        }
                        break;
                    case 1:
                        if (!this.mergeItemStack(stack, 1, 2, false)) {
                            return null;
                        }
                        break;
                    case 2:
                        if (!this.mergeItemStack(stack, 2, 3, false)) {
                            return null;
                        }
                        break;
                    case 3:
                        if (!this.mergeItemStack(stack, 3, 4, false)) {
                            return null;
                        }
                        break;
                    case 4:
                        if (!this.mergeItemStack(stack, 4, 5, false)) {
                            return null;
                        }
                        break;
                    case 5:
                        if (!this.mergeItemStack(stack, 5, 6, false)) {
                            return null;
                        }
                        break;
                    default:
                        return null;
                    }
                } else {
                    return null;
                }
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onPickupFromSlot(player, stack);

            return result;
        }
        return null;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
    
    public TileEntityMannequin getTileEntity() {
        return tileEntity;
    }
}
