package riskyken.armourersWorkshop.common.inventory;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.GuiArmourLibrary;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.inventory.slot.ISlotChanged;
import riskyken.armourersWorkshop.common.inventory.slot.SlotEquipmentSkinTemplate;
import riskyken.armourersWorkshop.common.inventory.slot.SlotOutput;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class ContainerArmourLibrary extends Container implements ISlotChanged {

    private TileEntityArmourLibrary tileEntity;
    
    public ContainerArmourLibrary(InventoryPlayer invPlayer, TileEntityArmourLibrary tileEntity) {
        this.tileEntity = tileEntity;

        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(invPlayer, x, 6 + 18 * x, 232));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 6 + 18 * x, 174 + y * 18));
            }
        }
        
        if (!tileEntity.isCreativeLibrary()) {
            addSlotToContainer(new SlotEquipmentSkinTemplate(tileEntity, 0, 226, 101, this));
        }
        addSlotToContainer(new SlotOutput(tileEntity, 1, 226, 137));
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        Slot slot = getSlot(slotID);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            if (slotID < 36) {
                if ((
                        stack.getItem() instanceof ItemEquipmentSkinTemplate & stack.getItemDamage() == 0) |
                        stack.getItem() instanceof ItemEquipmentSkin) {
                    if (!this.mergeItemStack(stack, 36, 37, false)) {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(stack, 9, 36, false)) {
                    if (!this.mergeItemStack(stack, 0, 9, false)) {
                        return null;
                    }
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
        if (screen != null && screen instanceof GuiArmourLibrary) {
            GuiArmourLibrary libScreen = (GuiArmourLibrary) screen;
            ItemStack stack = getSlot(36).getStack();
            if (stack == null) {
                libScreen.setFileName("");
            } else {
                SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
                if (skinPointer != null) {
                    if (ClientSkinCache.INSTANCE.isSkinInCache(skinPointer.getSkinId())) {
                        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer.getSkinId());
                        String skinName = skin.getCustomName();
                        if (StringUtils.isNoneBlank(skinName)) {
                            libScreen.setFileName(skinName);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }
    
    public TileEntityArmourLibrary getTileEntity() {
        return tileEntity;
    }
    
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object player : crafters) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMp = (EntityPlayerMP) player;
                ArmourersWorkshop.proxy.libraryManager.syncLibraryWithPlayer(playerMp);
            }
        }
    }
}
