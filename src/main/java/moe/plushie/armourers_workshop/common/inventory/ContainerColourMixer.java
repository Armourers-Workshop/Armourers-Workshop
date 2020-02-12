package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.api.common.painting.IPaintingTool;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotColourTool;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourMixer;
import moe.plushie.armourers_workshop.utils.UtilColour.ColourFamily;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerColourMixer extends ModContainer {

    private TileEntityColourMixer tileEntityColourMixer;

    public ContainerColourMixer(InventoryPlayer invPlayer, TileEntityColourMixer tileEntityColourMixer) {
        super(invPlayer);
        this.tileEntityColourMixer = tileEntityColourMixer;

        addSlotToContainer(new SlotColourTool(tileEntityColourMixer, 0, 83, 101));
        addSlotToContainer(new SlotOutput(tileEntityColourMixer, 1, 134, 101));

        addPlayerSlots(48, 158);
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        if (isSlotPlayerInv(index)) {
            Slot slot = getSlot(index);
            if (slot.getHasStack()) {
                ItemStack stack = slot.getStack();
                ItemStack result = stack.copy();

                if (stack.getItem() instanceof IPaintingTool) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
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
        return ItemStack.EMPTY;
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
            IContainerListener crafter = listeners.get(i);
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
