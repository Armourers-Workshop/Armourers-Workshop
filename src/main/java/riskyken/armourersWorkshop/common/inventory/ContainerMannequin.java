package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.inventory.slot.SlotMannequin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ContainerMannequin extends Container {

    private TileEntityMannequin tileEntity;
    
    public ContainerMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        this.tileEntity = tileEntity;
        
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(invPlayer, x, 8 + 18 * x, 232));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(invPlayer, x + y * 9 + 9, 8 + 18 * x, 174 + y * 18));
            }
        }
        
        for (int i = 0; i < 5; i++) {
            for (int y = 0; y < MannequinSlotType.values().length; y++) {
                addSlotToContainer(new SlotMannequin(MannequinSlotType.getOrdinal(y), tileEntity, y + i * 7, 5 + 19 * y, 5 + i * 19));
            }
        }
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            if (slotId > 35) {
                // Moving from mannequin to player.
                if (!this.mergeItemStack(stack, 9, 36, false)) {
                    if (!this.mergeItemStack(stack, 0, 9, false)) {
                        return null;
                    }
                }
            } else {
                // Moving from player to mannequin.
                boolean slotted = false;
                for (int i = 0; i < TileEntityMannequin.INVENTORY_SIZE; i++) {
                    int targetSlotId = i + 36;
                    Slot targetSlot = getSlot(targetSlotId);
                    boolean handSlot = false;
                    if (i % 7 == 4) {
                        handSlot = true;
                    }
                    if (i % 7 == 5) {
                        handSlot = true;
                    }
                    
                    ISkinType skinType = SkinNBTHelper.getSkinTypeFromStack(stack);
                    
                    if (skinType != null && skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings) {
                        if (!handSlot) {
                            if (targetSlot.isItemValid(stack)) {
                                if (this.mergeItemStack(stack, targetSlotId, targetSlotId + 1, false)) {
                                    slotted = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        if (targetSlot.isItemValid(stack)) {
                            if (this.mergeItemStack(stack, targetSlotId, targetSlotId + 1, false)) {
                                slotted = true;
                                break;
                            }
                        }
                    }
                }
                if (!slotted) {
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
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUsableByPlayer(player);
    }
    
    public TileEntityMannequin getTileEntity() {
        return tileEntity;
    }
}
