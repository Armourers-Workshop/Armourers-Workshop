package riskyken.armourers_workshop.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourers_workshop.common.inventory.slot.SlotHidable;
import riskyken.armourers_workshop.common.inventory.slot.SlotOutput;
import riskyken.armourers_workshop.common.network.PacketHandler;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin.SendType;
import riskyken.armourers_workshop.common.skin.cache.CommonSkinCache;
import riskyken.armourers_workshop.common.skin.data.Skin;
import riskyken.armourers_workshop.common.skin.data.SkinPointer;
import riskyken.armourers_workshop.common.tileentities.TileEntityGlobalSkinLibrary;
import riskyken.armourers_workshop.utils.SkinNBTHelper;

public class ContainerGlobalSkinLibrary extends Container implements IButtonPress {
    
    private TileEntityGlobalSkinLibrary tileEntity;
    private EntityPlayer player;
    private IInventory inventory;
    
    public ContainerGlobalSkinLibrary(InventoryPlayer invPlayer, TileEntityGlobalSkinLibrary tileEntity) {
        this.tileEntity = tileEntity;
        this.player = invPlayer.player;
        inventory = new ModInventory("fakeInventory", 2);
        int playerInvY = 20;
        int hotBarY = playerInvY + 58;
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new SlotHidable(invPlayer, x, 5 + 18 * x, hotBarY));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new SlotHidable(invPlayer, x + y * 9 + 9, 5 + 18 * x, playerInvY + y * 18));
            }
        }
        addSlotToContainer(new SlotHidable(inventory, 0, 5, 5));
        addSlotToContainer(new SlotOutput(inventory, 1, 5, 5));
    }
    
    public TileEntityGlobalSkinLibrary getTileEntity() {
        return tileEntity;
    }
    
    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        super.onContainerClosed(entityPlayer);
        if (!tileEntity.getWorld().isRemote) {
            Slot slot = getSlot(36);
            if (slot.getHasStack()) {
                // TODO entityPlayer.dropPlayerItemWithRandomChoice(slot.getStack(), false);
            }
            slot = getSlot(37);
            if (slot.getHasStack()) {
             // TODO entityPlayer.dropPlayerItemWithRandomChoice(slot.getStack(), false);
            }
        }
    }
    
    public void onSkinUploaded() {
        if (!tileEntity.getWorld().isRemote) {
            ItemStack stack = getSlot(36).getStack();
            getSlot(36).putStack(null);
            getSlot(37).putStack(stack);
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return entityPlayer.getDistanceSq(tileEntity.getPos()) <= 64 & !entityPlayer.isDead;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotId) {
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
                if (!this.mergeItemStack(stack, 36, 37, false)) {
                    return null;
                }
            }
            
            if (stack.getCount() == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            slot.onTake(entityPlayer, stack);
            
            return result;
        }
        return null;
    }

    @Override
    public void buttonPressed(byte buttonId) {
        if (buttonId == 0) {
            if (!tileEntity.getWorld().isRemote) {
                if (!getSlot(37).getHasStack()) {
                    ItemStack itemStack = getSlot(36).getStack();
                    SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(itemStack);
                    if (skinPointer != null) {
                        Skin skin = CommonSkinCache.INSTANCE.getSkin(skinPointer);
                        if (skin != null) {
                            onSkinUploaded();
                            MessageServerLibrarySendSkin message = new MessageServerLibrarySendSkin(null, null, skin, SendType.GLOBAL_UPLOAD);
                            PacketHandler.networkWrapper.sendTo(message, (EntityPlayerMP) player);
                        }
                    }
                }
            }
        }
    }
}
