package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.gui.skinlibrary.GuiSkinLibrary;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.init.items.ItemSkin;
import moe.plushie.armourers_workshop.common.init.items.ItemSkinTemplate;
import moe.plushie.armourers_workshop.common.inventory.slot.ISlotChanged;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotOutput;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotSkinTemplate;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinLibrary;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSkinLibrary extends ModTileContainer<TileEntitySkinLibrary> implements ISlotChanged {

    public ContainerSkinLibrary(InventoryPlayer invPlayer, TileEntitySkinLibrary tileEntity) {
        super(invPlayer, tileEntity);
        addPlayerSlots(6, 174);
        addSlotToContainer(new SlotSkinTemplate(tileEntity, 0, 226, 101, this));
        addSlotToContainer(new SlotOutput(tileEntity, 1, 226, 137));
    }
    
    @Override
    protected ItemStack transferStackFromPlayer(EntityPlayer playerIn, int index) {
        Slot slot = getSlot(index);
        if (slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            
            if ((stack.getItem() instanceof ItemSkinTemplate) | stack.getItem() instanceof ItemSkin) {
                if (!this.mergeItemStack(stack, getPlayerInvEndIndex(), getPlayerInvEndIndex() + 1, false)) {
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
    
    @Override
    public void onSlotChanged(int slotId) {
        if (!ArmourersWorkshop.isDedicated()) {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                updateSkinName(slotId);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void updateSkinName(int slotId) {
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen screen = mc.currentScreen;
        if (screen != null && screen instanceof GuiSkinLibrary) {
            GuiSkinLibrary libScreen = (GuiSkinLibrary) screen;
            ItemStack stack = getSlot(36).getStack();
            if (stack == null) {
                libScreen.setFileName("");
            } else {
                SkinDescriptor skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
                if (skinPointer != null) {
                    if (ClientSkinCache.INSTANCE.isSkinInCache(skinPointer)) {
                        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
                        String skinName = skin.getCustomName();
                        if (!StringUtils.isNullOrEmpty(skinName)) {
                            libScreen.setFileName(skinName);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object player : listeners) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMp = (EntityPlayerMP) player;
                ArmourersWorkshop.getProxy().getLibraryManager().syncLibraryWithPlayer(playerMp);
            }
        }
    }
}
