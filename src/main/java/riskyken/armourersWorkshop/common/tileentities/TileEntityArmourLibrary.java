package riskyken.armourersWorkshop.common.tileentities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.items.ItemArmourContainerItem;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerLibrarySendSkin;
import riskyken.armourersWorkshop.common.skin.ISkinHolder;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public class TileEntityArmourLibrary extends AbstractTileEntityInventory implements ISidedInventory {
    
    public ArrayList<String> serverFileNames = null;
    public ArrayList<String> clientFileNames = null;
    
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
        
        if (!EquipmentNBTHelper.stackHasSkinData(stackInput)) {
            return;
        }
        
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stackInput);
        
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
     */
    public void saveArmour(String fileName, EntityPlayerMP player) {
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
        
        if (!EquipmentNBTHelper.stackHasSkinData(stackInput)) {
            return;
        }
        
        int equipmentId = EquipmentNBTHelper.getSkinIdFromStack(stackInput);
        
        Skin skin = SkinDataCache.INSTANCE.getEquipmentData(equipmentId);
        if (skin == null) {
            return;
        }
        
        if (!SkinIOUtils.saveSkinFromFileName(fileName + ".armour", skin)) {
            return;
        }
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }
    
    /**
     * Loads an armour file from the disk and adds it to an items NBT data.
     * @param filename The name of the file to load.
     * @param player The player that pressed the load button.
     */
    public void loadArmour(String fileName, EntityPlayerMP player) {
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
        
        
        Skin armourItemData = SkinIOUtils.loadSkinFromFileName(fileName + ".armour");
        if (armourItemData == null) {
            return;
        }
        
        SkinDataCache.INSTANCE.addEquipmentDataToCache(armourItemData);
        
        ItemStack stackArmour = EquipmentNBTHelper.makeEquipmentSkinStack(armourItemData);
        
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
        
        ItemStack inputItem = EquipmentNBTHelper.makeEquipmentSkinStack(itemData);
        if (inputItem == null) {
            return;
        }
        
        SkinDataCache.INSTANCE.addEquipmentDataToCache(itemData);
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, inputItem);
    }
    
    public static ArrayList<String> getFileNames(boolean addSkinTypes) {
        ArrayList<String> files = new ArrayList<String>();
        
        File directory = SkinIOUtils.getSkinLibraryDirectory();
        if (!directory.exists()) {
            return null;
        }
        
        File[] templateFiles;
        try {
            templateFiles = directory.listFiles();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, "Armour file list load failed.");
            e.printStackTrace();
            return null;
        }
        
        for (int i = 0; i < templateFiles.length; i++) {
            if (templateFiles[i].getName().endsWith(".armour")) {
                String cleanName = FilenameUtils.removeExtension(templateFiles[i].getName());
                if (addSkinTypes) {
                    String skinTypeName = SkinIOUtils.getSkinTypeNameFromFile(templateFiles[i]);
                    files.add(cleanName + "\n" + skinTypeName);
                } else {
                    files.add(cleanName);
                }
            }
        }
        Collections.sort(files);
        return files;
    }
    
    public void setArmourList(ArrayList<String> fileNames) {
        this.serverFileNames = fileNames;
        this.clientFileNames = getFileNames(true);
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
        if (stack.getItem() instanceof ItemArmourContainerItem) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }
}
