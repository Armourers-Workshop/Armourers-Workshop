package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotDyeBottle;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotDyeableSkin;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityDyeTable;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import moe.plushie.armourers_workshop.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDyeTable extends Container {

    private final InventoryPlayer invPlayer;
    private final TileEntityDyeTable tileEntity;
    private boolean instanced;
    private IInventory inventory;

    public ContainerDyeTable(InventoryPlayer invPlayer, TileEntityDyeTable tileEntity) {
        this.invPlayer = invPlayer;
        this.tileEntity = tileEntity;

        instanced = ConfigHandler.instancedDyeTable;
        if (instanced) {
            inventory = new ModInventory("fakeInventory", 10);
        } else {
            inventory = tileEntity;
        }

        int playerInvY = 108;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, playerInvY + y * 18));
            }
        }
        
        addSlotToContainer(new SlotDyeableSkin(inventory, 0, 26, 23, this));

        addSlotToContainer(new SlotDyeBottle(inventory, 1, 68, 36, this));
        addSlotToContainer(new SlotDyeBottle(inventory, 2, 90, 36, this));
        addSlotToContainer(new SlotDyeBottle(inventory, 3, 112, 36, this));
        addSlotToContainer(new SlotDyeBottle(inventory, 4, 134, 36, this));
        addSlotToContainer(new SlotDyeBottle(inventory, 5, 68, 58, this));
        addSlotToContainer(new SlotDyeBottle(inventory, 6, 90, 58, this));
        addSlotToContainer(new SlotDyeBottle(inventory, 7, 112, 58, this));
        addSlotToContainer(new SlotDyeBottle(inventory, 8, 134, 58, this));

        addSlotToContainer(new SlotOutput(inventory, 9, 26, 69, this));

        ItemStack stack = getInputSlot().getStack();

        if (!stack.isEmpty()) {
            updateLockedSlots(stack);
        }
    }

    public Slot getInputSlot() {
        return getSlot(36);
    }

    public Slot getOutputSlot() {
        return getSlot(45);
    }

    public Slot getDyeSlot(int index) {
        return getSlot(37 + index);
    }

    public void skinAdded(ItemStack stack) {
        updateLockedSlots(stack);
        if (tileEntity.getWorld().isRemote) {
            return;
        }

        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        ISkinDye dye = skinPointer.getSkinDye();
        skinRemoved();
        updateLockedSlots(stack);
        putStackInSlot(45, stack.copy());
        putDyesInSlots();
        detectAndSendChanges();

    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!tileEntity.getWorld().isRemote & instanced) {
            // Drop dye bottles.
            for (int i = 0; i < 8; i++) {
                SlotDyeBottle slot = (SlotDyeBottle) getSlot(37 + i);
                if (!slot.isLocked()) {
                    UtilItems.spawnItemAtEntity(entityPlayer, slot.getStack(), true);
                }
            }

            // Drop output slot.
            Slot slot = getOutputSlot();
            if (slot.getHasStack()) {
                UtilItems.spawnItemAtEntity(entityPlayer, slot.getStack(), true);
            }
        }
    }

    public void skinRemoved() {
        if (!tileEntity.getWorld().isRemote) {
            for (int i = 0; i < 8; i++) {
                SlotDyeBottle slot = (SlotDyeBottle) getSlot(37 + i);
                if (!slot.isLocked()) {
                    PlayerUtils.giveItem(invPlayer.player, getSlot(37 + i).getStack());
                } else {
                    slot.setLocked(false);
                }
                putStackInSlot(37 + i, ItemStack.EMPTY);
            }
            putStackInSlot(45, ItemStack.EMPTY);
            detectAndSendChanges();
        }
        unlockedSlots();
    }

    /**
     * Reads the input slot and locks dye slots that are in use.
     * 
     * @param stack
     */
    private void updateLockedSlots(ItemStack stack) {

        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        ISkinDye dye = skinPointer.getSkinDye();
        for (int i = 0; i < 8; i++) {
            if (dye.haveDyeInSlot(i)) {
                ((SlotDyeBottle) getSlot(37 + i)).setLocked(true);
            } else {
                ((SlotDyeBottle) getSlot(37 + i)).setLocked(false);
            }
        }

    }

    private void unlockedSlots() {
        for (int i = 0; i < 8; i++) {
            ((SlotDyeBottle) getSlot(37 + i)).setLocked(false);
        }
    }

    /**
     * Reads the output slot and adds dye bottles to their slots.
     */
    private void putDyesInSlots() {
        if (tileEntity.getWorld().isRemote) {
            return;
        }
        ItemStack outStack = getSlot(45).getStack();
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(outStack);
        ISkinDye dye = skinPointer.getSkinDye();
        for (int i = 0; i < 8; i++) {
            if (dye.haveDyeInSlot(i)) {
                byte[] rgbt = dye.getDyeColour(i);
                ItemStack bottle = new ItemStack(ModItems.DYE_BOTTLE, 1, 1);
                PaintingHelper.setToolPaintColour(bottle, rgbt);
                PaintingHelper.setToolPaint(bottle, PaintTypeRegistry.getInstance().getPaintTypeFormByte(rgbt[3]));
                if (dye.hasName(i)) {
                    bottle.setStackDisplayName(dye.getDyeName(i));
                }
                putStackInSlot(37 + i, bottle);
            } else {
                putStackInSlot(37 + i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        for (int i = 0; i < 9; i++) {
            putStackInSlot(37 + i, ItemStack.EMPTY);
        }
        putStackInSlot(36, ItemStack.EMPTY);
        super.onCraftMatrixChanged(inventory);
    }

    public void dyeAdded(ItemStack dyeStack, int slotId) {
        ItemStack skinStack = getSlot(45).getStack();
        if (skinStack.isEmpty()) {
            return;
        }
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(skinStack);

        ISkinDye skinDye = skinPointer.getSkinDye();

        byte[] rgbt = PaintingHelper.getToolPaintData(dyeStack);
        String name = null;
        if (dyeStack.hasDisplayName()) {
            name = dyeStack.getDisplayName();
        }
        skinDye.addDye(slotId, rgbt, name);

        SkinNBTHelper.addSkinDataToStack(skinStack, skinPointer);
    }

    public void dyeRemoved(int slotId) {
        ItemStack skinStack = getSlot(45).getStack();
        if (skinStack.isEmpty()) {
            return;
        }
        SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(skinStack);
        ISkinDye skinDye = skinPointer.getSkinDye();
        skinDye.removeDye(slotId);
        SkinNBTHelper.addSkinDataToStack(skinStack, skinPointer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUsableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
        Slot slot = getSlot(slotId);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotId > 35) {
                // Moving from tile entity to player.
                if (!this.mergeItemStack(stack, 9, 36, false)) {
                    if (!this.mergeItemStack(stack, 0, 9, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // Moving from player to tile entity.
                SkinDescriptor sp = SkinNBTHelper.getSkinDescriptorFromStack(stack);
                if (sp != null) {
                    if (!this.mergeItemStack(stack, 36, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() == ModItems.DYE_BOTTLE && getSlot(36).getHasStack() & PaintingHelper.getToolHasPaint(stack)) {
                    if (!this.mergeItemStack(stack, 37, 45, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(player, stack);

            return result;
        }
        return ItemStack.EMPTY;
    }
}
