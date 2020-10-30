package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin.SendType;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityGlobalSkinLibrary;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerGlobalSkinLibrary extends ModTileContainer<TileEntityGlobalSkinLibrary> implements IButtonPress {

    private final IInventory inventory;
    private final EntityPlayer player;

    public ContainerGlobalSkinLibrary(InventoryPlayer invPlayer, TileEntityGlobalSkinLibrary tileEntity) {
        super(invPlayer, tileEntity);
        inventory = new ModInventory("fakeInventory", 2);
        player = invPlayer.player;
        addPlayerSlots(5, 20);
        addSlotToContainer(new SlotHidable(inventory, 0, 5, 5));
        addSlotToContainer(new SlotOutput(inventory, 1, 5, 5));
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!tileEntity.getWorld().isRemote) {
            Slot slot = getInputSlot();
            if (slot.getHasStack()) {
                UtilItems.spawnItemAtEntity(entityPlayer, slot.getStack(), true);
            }
            slot = getOutputSlot();
            if (slot.getHasStack()) {
                UtilItems.spawnItemAtEntity(entityPlayer, slot.getStack(), true);
            }
        }
    }

    public Slot getInputSlot() {
        return getSlot(36);
    }

    public Slot getOutputSlot() {
        return getSlot(37);
    }

    public void onSkinUploaded() {
        if (!tileEntity.getWorld().isRemote) {
            ItemStack stack = getSlot(36).getStack();
            getInputSlot().putStack(ItemStack.EMPTY);
            getOutputSlot().putStack(stack);
        }
    }

    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();

            // Moving from player to tile entity.
            if (!this.mergeItemStack(stack, 36, 37, false)) {
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

    @Override
    public void buttonPressed(EntityPlayerMP player, byte buttonId) {
        if (buttonId == 0) {
            if (!tileEntity.getWorld().isRemote) {
                if (!getSlot(37).getHasStack()) {
                    ItemStack itemStack = getSlot(36).getStack();
                    SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
                    if (skinPointer != null) {
                        Skin skin = CommonSkinCache.INSTANCE.getSkin(skinPointer);
                        if (skin != null) {
                            onSkinUploaded();
                            MessageServerLibrarySendSkin message = new MessageServerLibrarySendSkin(null, null, skin, SendType.GLOBAL_UPLOAD);
                            PacketHandler.networkWrapper.sendTo(message, player);
                        }
                    }
                }
            }
        }
    }
}
