package riskyken.armourersWorkshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.inventory.slot.SlotDyeBottle;
import riskyken.armourersWorkshop.common.inventory.slot.SlotDyeableSkin;
import riskyken.armourersWorkshop.common.inventory.slot.SlotOutput;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityDyeTable;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import riskyken.armourersWorkshop.utils.UtilPlayer;

public class ContainerDyeTable extends Container {

    private final InventoryPlayer invPlayer;
    private final TileEntityDyeTable tileEntity;
    
    public ContainerDyeTable(InventoryPlayer invPlayer, TileEntityDyeTable tileEntity) {
        this.invPlayer = invPlayer;
        this.tileEntity = tileEntity;
        
        int playerInvY = 108;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 48 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 48 + 18 * x, playerInvY + y * 18));
            }
        }
        
        
        addSlotToContainer(new SlotDyeableSkin(tileEntity, 0, 26, 23, this));
        
        addSlotToContainer(new SlotDyeBottle(tileEntity, 1, 68, 36, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 2, 90, 36, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 3, 112, 36, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 4, 134, 36, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 5, 68, 58, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 6, 90, 58, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 7, 112, 58, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 8, 134, 58, this));
        
        addSlotToContainer(new SlotOutput(tileEntity, 9, 26, 69, this));
        
        ItemStack stack = getSlot(36).getStack();
        
        if (stack != null) {
            updateLockedSlots(stack);
        }
    }
    
    
    public void skinAdded(ItemStack stack) {
        updateLockedSlots(stack);
        if (tileEntity.getWorldObj().isRemote) {
            return;
        }
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        ISkinDye dye = skinPointer.getSkinDye();
        skinRemoved();
        updateLockedSlots(stack);
        putStackInSlot(45, stack.copy());
        putDyesInSlots();
        detectAndSendChanges();
    }
    
    public void skinRemoved() {
        if (!tileEntity.getWorldObj().isRemote) {
            for (int i = 0; i < 8; i++) {
                SlotDyeBottle slot = (SlotDyeBottle) getSlot(37 + i);
                if (!slot.isLocked()) {
                    UtilPlayer.giveItem(invPlayer.player, getSlot(37 + i).getStack());
                } else {
                    slot.setLocked(false);
                }
                putStackInSlot(37 + i, null);
            }
            putStackInSlot(45, null);
            detectAndSendChanges();
        }
        unlockedSlots();
    }
    
    /**
     * Reads the input slot and locks dye slots that are in use.
     * @param stack
     */
    private void updateLockedSlots(ItemStack stack) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        ISkinDye dye = skinPointer.getSkinDye();
        for (int i = 0; i < 8; i++) {
            if (dye.haveDyeInSlot(i)) {
                ModLogger.log("locking slot " + i);
                ((SlotDyeBottle)getSlot(37 + i)).setLocked(true);
            } else {
                ModLogger.log("unlocking slot " + i);
                ((SlotDyeBottle)getSlot(37 + i)).setLocked(false);
            }
        }
    }
    
    private void unlockedSlots() {
        for (int i = 0; i < 8; i++) {
            ((SlotDyeBottle)getSlot(37 + i)).setLocked(false);
        }
    }
    
    /**
     * Reads the output slot and adds dye bottles to their slots.
     */
    private void putDyesInSlots() {
        if (tileEntity.getWorldObj().isRemote) {
            return;
        }
        ItemStack outStack = getSlot(45).getStack();
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(outStack);
        ISkinDye dye = skinPointer.getSkinDye();
        for (int i = 0; i < 8; i++) {
            if (dye.haveDyeInSlot(i)) {
                byte[] rgbt = dye.getDyeColour(i);
                ItemStack bottle = new ItemStack(ModItems.dyeBottle, 1, 1);
                PaintingHelper.setToolPaintColour(bottle, rgbt);
                PaintingHelper.setToolPaint(bottle, PaintType.getPaintTypeFormSKey(rgbt[3]));
                putStackInSlot(37 + i, bottle);
            } else {
                putStackInSlot(37 + i, null);
            }
        }
    }
    
    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        for (int i = 0; i < 9; i++) {
            putStackInSlot(37 + i, null);
        }
        putStackInSlot(36, null);
        super.onCraftMatrixChanged(inventory);
    }
    
    public void dyeAdded(ItemStack dyeStack, int slotId) {
        ItemStack skinStack = getSlot(45).getStack();
        if (skinStack == null) {
            return;
        }
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(skinStack);
        
        ISkinDye skinDye = skinPointer.getSkinDye();
        
        byte[] rgbt = PaintingHelper.getToolPaintData(dyeStack);
        skinDye.addDye(slotId, rgbt);
        
        SkinNBTHelper.addSkinDataToStack(skinStack, skinPointer);
    }
    
    public void dyeRemoved(int slotId) {
        ItemStack skinStack = getSlot(45).getStack();
        if (skinStack == null) {
            return;
        }
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(skinStack);
        ISkinDye skinDye = skinPointer.getSkinDye();
        skinDye.removeDye(slotId);
        SkinNBTHelper.addSkinDataToStack(skinStack, skinPointer);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            
            if (slotId > 35) {
                //Moving from tile entity to player.
                if (!this.mergeItemStack(stack, 9, 36, false)) {
                    if (!this.mergeItemStack(stack, 0, 9, false)) {
                        return null;
                    }
                }
            } else {
              //Moving from player to tile entity.
                if (stack.getItem() == ModItems.equipmentSkin) {
                    if (!this.mergeItemStack(stack, 36, 37, false)) {
                        return null;
                    }
                } else if (stack.getItem() == ModItems.dyeBottle && getSlot(36).getHasStack() & PaintingHelper.getToolHasPaint(stack)) {
                    if (!this.mergeItemStack(stack, 37, 45, false)) {
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
}
