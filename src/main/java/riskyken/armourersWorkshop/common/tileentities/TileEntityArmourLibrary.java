package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerLibrarySendSkin;
import riskyken.armourersWorkshop.common.skin.ISkinHolder;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class TileEntityArmourLibrary extends AbstractTileEntityInventory implements ISidedInventory {
    
    public TileEntityArmourLibrary() {
        this.items = new ItemStack[2];
    }
    
    @Override
    public String getInventoryName() {
        return LibBlockNames.ARMOUR_LIBRARY;
    }
    
    public boolean isCreativeLibrary() {
        int meta = getBlockMetadata();
        if (meta == 1) {
            return true;
        }
        return false;
    }
    
    public void sendArmourToClient(String filename, EntityPlayerMP player) {
        if (!ConfigHandler.allowClientsToDownloadSkins) {
            return;
        }
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (stackInput == null) {
            return;
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!(stackInput.getItem() instanceof ItemEquipmentSkin)) {
            return;
        }
        
        if (!SkinNBTHelper.stackHasSkinData(stackInput)) {
            return;
        }
        
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stackInput);
        
        Skin skin = SkinDataCache.INSTANCE.getEquipmentData(skinPointer.skinId);
        if (skin == null) {
            return;
        }
        
        MessageServerLibrarySendSkin message = new MessageServerLibrarySendSkin(filename, skin);
        PacketHandler.networkWrapper.sendTo(message, player);
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }

    /**
     * Save armour data from an items NBT data into a file on the disk.
     * @param filename The name of the file to save to.
     * @param player The player that pressed the save button.
     * @param publicFiles If true save to the public file list or false for the players private files.
     */
    public void saveArmour(String fileName, EntityPlayerMP player, boolean publicFiles) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        if (fileName.contains("/") | fileName.contains("\\")) {
            ModLogger.log(String.format("Player %s tried to save a file with invalid characters in the file name.",
                    player.getCommandSenderName()));
            ModLogger.log(String.format("The file name was: %s", fileName));
            return;
        }
        
        if (stackInput == null) {
            return;
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!(stackInput.getItem() instanceof ItemEquipmentSkin)) {
            return;
        }
        
        if (!SkinNBTHelper.stackHasSkinData(stackInput)) {
            return;
        }
        
        int equipmentId = SkinNBTHelper.getSkinIdFromStack(stackInput);
        
        Skin skin = SkinDataCache.INSTANCE.getEquipmentData(equipmentId);
        if (skin == null) {
            return;
        }
        
        boolean saved = false;
        if (publicFiles) {
            saved = SkinIOUtils.saveSkinFromFileName(fileName + ".armour", skin);
        } else {
            saved = SkinIOUtils.saveSkinFromFileName(fileName + ".armour", skin, player);
        }
        if (!saved) {
            return;
        }
        
        if (ArmourersWorkshop.isDedicated()) {
            if (publicFiles) {
                ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, skin.getSkinType()), LibraryFileType.SERVER_PUBLIC, player);
            } else {
                ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, skin.getSkinType()), LibraryFileType.SERVER_PRIVATE, player);
            }
        } else {
            ArmourersWorkshop.proxy.libraryManager.addFileToListType(new LibraryFile(fileName, skin.getSkinType()), LibraryFileType.LOCAL, player);
        }

        
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }
    
    /**
     * Loads an armour file from the disk and adds it to an items NBT data.
     * @param filename The name of the file to load.
     * @param player The player that pressed the load button.
     * @param publicFiles If true load from the public file list or false for the players private files.
     */
    public void loadArmour(String fileName, EntityPlayerMP player, boolean publicFiles) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        if (fileName.contains("/") | fileName.contains("\\")) {
            ModLogger.log(String.format("Player %s tried to load a file with invalid characters in the file name.",
                    player.getCommandSenderName()));
            ModLogger.log(String.format("The file name was: %s", fileName));
            return;
        }
        if (!isCreativeLibrary()) {
            if (stackInput == null) {
                return;
            }
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!isCreativeLibrary()) {
            if (!(stackInput.getItem() instanceof ISkinHolder)) {
                return;
            }
        }
        
        
        Skin armourItemData = null;
        if (publicFiles) {
            armourItemData = SkinIOUtils.loadSkinFromFileName(fileName + ".armour");
        } else {
            armourItemData = SkinIOUtils.loadSkinFromFileName(fileName + ".armour", player);
        }
        
        if (armourItemData == null) {
            return;
        }
        
        SkinDataCache.INSTANCE.addEquipmentDataToCache(armourItemData);
        
        ItemStack stackArmour = SkinNBTHelper.makeEquipmentSkinStack(armourItemData);
        
        if (stackArmour == null) {
            return;
        }
        
        if (!isCreativeLibrary()) {
            this.decrStackSize(0, 1);
        }
        
        this.setInventorySlotContents(1, stackArmour);
    }
    
    public void loadArmour(Skin itemData, EntityPlayerMP player) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (!isCreativeLibrary()) {
            if (stackInput == null) {
                return;
            }
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!isCreativeLibrary()) {
            if (!(stackInput.getItem() instanceof ISkinHolder)) {
                return;
            }
        }
        
        ItemStack inputItem = SkinNBTHelper.makeEquipmentSkinStack(itemData);
        if (inputItem == null) {
            return;
        }
        
        SkinDataCache.INSTANCE.addEquipmentDataToCache(itemData);
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, inputItem);
    }
    
    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (isCreativeLibrary()) {
            int[] slots = new int[1];
            slots[0] = 1;
            return slots;
        } else {
            int[] slots = new int[2];
            slots[0] = 0;
            slots[1] = 1;
            return slots;
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        if (isCreativeLibrary()) {
            return false;
        }
        if (slot != 0) {
            return false;
        }
        if (stack.getItem() instanceof ItemEquipmentSkinTemplate && stack.getItemDamage() == 0) {
            return true;
        }
        if (stack.getItem() instanceof ItemEquipmentSkin) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }
}
