package riskyken.armourersWorkshop.common.inventory;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.inventory.slot.SlotDyeBottle;
import riskyken.armourersWorkshop.common.inventory.slot.SlotDyeableSkin;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityDyeTable;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.ModLogger;

public class ContainerDyeTable extends Container {

    private final TileEntityDyeTable tileEntity;
    
    public ContainerDyeTable(InventoryPlayer invPlayer, TileEntityDyeTable tileEntity) {
        this.tileEntity = tileEntity;
        
        int playerInvY = 131;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
        
        addSlotToContainer(new SlotDyeableSkin(tileEntity, 0, 80, 59, this));
        
        addSlotToContainer(new SlotDyeBottle(tileEntity, 1, 80, 24, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 2, 105, 34, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 3, 116, 59, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 4, 105, 84, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 5, 80, 94, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 6, 55, 84, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 7, 44, 59, this));
        addSlotToContainer(new SlotDyeBottle(tileEntity, 8, 55, 34, this));
    }
    
    public void skinAdded(ItemStack stack) {
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stack);
        ISkinDye dye = skinPointer.getSkinDye();
        ModLogger.log("added");
        for (int i = 0; i < 8; i++) {
            if (dye.haveDyeInSlot(i)) {
                ModLogger.log("making dye bottle for " + i);
                ItemStack bottle = new ItemStack(ModItems.dyeBottle, 1, 1);
                PaintingHelper.setToolPaintColour(bottle, dye.getDyeColour(i));
                tileEntity.setInventorySlotContents(i + 1, bottle);
            }
        }
    }
    
    public void skinRemoved() {
        for (int i = 0; i < 8; i++) {
            tileEntity.setInventorySlotContents(i + 1, null);
        }
    }
    
    public void dyeAdded(ItemStack dyeStack, int slotId) {
        ItemStack skinStack = tileEntity.getStackInSlot(0);
        if (skinStack == null) {
            return;
        }
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(skinStack);
        
        ISkinDye skinDye = skinPointer.getSkinDye();
        ModLogger.log("number of dyes before " + skinDye.getNumberOfDyes());
        
        byte[] rgbt = PaintingHelper.getToolPaintData(dyeStack);
        skinDye.addDye(slotId, rgbt);
        
        EquipmentNBTHelper.addSkinDataToStack(skinStack, skinPointer);
        
        ModLogger.log("adding dye " + Arrays.toString(rgbt));
        ModLogger.log("number of dyes after " + skinDye.getNumberOfDyes());
        
    }
    
    public void dyeRemoved(int slotId) {
        ItemStack skinStack = tileEntity.getStackInSlot(0);
        if (skinStack == null) {
            return;
        }
        ModLogger.log("removing dye");
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(skinStack);
        ISkinDye skinDye = skinPointer.getSkinDye();
        skinDye.removeDye(slotId);
        EquipmentNBTHelper.addSkinDataToStack(skinStack, skinPointer);
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        return null;
    }
}
