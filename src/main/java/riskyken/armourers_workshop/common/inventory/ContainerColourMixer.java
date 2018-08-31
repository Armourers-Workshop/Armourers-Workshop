package riskyken.armourers_workshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.api.common.painting.IPaintingTool;
import riskyken.armourers_workshop.common.inventory.slot.SlotColourTool;
import riskyken.armourers_workshop.common.inventory.slot.SlotOutput;
import riskyken.armourers_workshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourers_workshop.utils.UtilColour.ColourFamily;

public class ContainerColourMixer extends Container {

    private TileEntityColourMixer tileEntityColourMixer;

    public ContainerColourMixer(InventoryPlayer invPlayer,
            TileEntityColourMixer tileEntityColourMixer) {
        this.tileEntityColourMixer = tileEntityColourMixer;

        addSlotToContainer(new SlotColourTool(tileEntityColourMixer, 0, 144, 32));
        addSlotToContainer(new SlotOutput(tileEntityColourMixer, 1, 144, 73));

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 48 + 18 * x, 216));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 48 + 18 * x, 158 + y * 18));
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        Slot slot = getSlot(slotID);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            if (slotID < 2) {
                if (!this.mergeItemStack(stack, 11, 38, false)) {
                    if (!this.mergeItemStack(stack, 2, 11, false)) {
                        return null;
                    }
                }
            } else {
                if (stack.getItem() instanceof IPaintingTool) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return null;
                    }
                } else {
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
    
    private ColourFamily lastColourFamily;
    
    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, tileEntityColourMixer.getColourFamily().ordinal());
        lastColourFamily = tileEntityColourMixer.getColourFamily();
    }
    
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < listeners.size(); i++) {
            IContainerListener crafter = (IContainerListener) listeners.get(i);
            if (lastColourFamily != tileEntityColourMixer.getColourFamily()) {
                crafter.sendWindowProperty(this, 0, tileEntityColourMixer.getColourFamily().ordinal());
            }
        }
        lastColourFamily = tileEntityColourMixer.getColourFamily();
    }
    
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) {
            tileEntityColourMixer.setColourFamily(ColourFamily.values()[data]);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntityColourMixer.isUsableByPlayer(player);
    }

    public TileEntityColourMixer getTileEntity() {
        return tileEntityColourMixer;
    }
}
