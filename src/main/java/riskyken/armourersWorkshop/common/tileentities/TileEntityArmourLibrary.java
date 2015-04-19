package riskyken.armourersWorkshop.common.tileentities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.equipment.EquipmentDataCache;
import riskyken.armourersWorkshop.common.equipment.ISkinHolder;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.NewerFileVersionException;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.ModLogger;

public class TileEntityArmourLibrary extends AbstractTileEntityInventory {
    
    public ArrayList<String> serverFileNames = null;
    public ArrayList<String> clientFileNames = null;
    
    public TileEntityArmourLibrary() {
        this.items = new ItemStack[2];
    }
    
    @Override
    public String getInventoryName() {
        return LibBlockNames.ARMOUR_LIBRARY;
    }

    /**
     * Save armour data from an items NBT data into a file on the disk.
     * @param filename The name of the file to save to.
     * @param player The player that pressed the save button.
     */
    public void saveArmour(String filename, EntityPlayerMP player) {
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
        
        if (!EquipmentNBTHelper.itemStackHasCustomEquipment(stackInput)) {
            return;
        }
        
        int equipmentId = EquipmentNBTHelper.getEquipmentIdFromStack(stackInput);
        
        if (!createArmourDirectory()) { return; }

        File armourDir = new File(System.getProperty("user.dir"));
        armourDir = new File(armourDir, LibModInfo.ID);
        
        DataOutputStream stream = null;
        File targetFile = new File(armourDir, File.separatorChar + filename + ".armour");
        
        CustomEquipmentItemData equipmentData = EquipmentDataCache.INSTANCE.getEquipmentData(equipmentId);
        if (equipmentData == null) {
            return;
        }
        
        try {
            stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)));
            equipmentData.writeToStream(stream);
            stream.flush();
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Armour file not found.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Armour file save failed.");
            e.printStackTrace();
            return;
        } finally {
            IOUtils.closeQuietly(stream);
        }
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }
    
    /**
     * Loads an armour file from the disk and adds it to an items NBT data.
     * @param filename The name of the file to load.
     * @param player The player that pressed the load button.
     */
    public void loadArmour(String filename, EntityPlayerMP player) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (stackInput == null) {
            return;
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!(stackInput.getItem() instanceof ISkinHolder)) {
            return;
        }
        ISkinHolder inputItem = (ISkinHolder)stackInput.getItem();
        
        CustomEquipmentItemData armourItemData = loadCustomArmourItemDataFromFile(filename);
        if (armourItemData == null) {
            return;
        }

        ItemStack stackArmour = inputItem.makeStackForEquipment(armourItemData);
        if (stackArmour == null) {
            return;
        }
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackArmour);
    }
    
    public void loadArmour(CustomEquipmentItemData itemData, EntityPlayerMP player) {
        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (stackInput == null) {
            return;
        }
        
        if (stackOutput != null) {
            return;
        }
        
        if (!(stackInput.getItem() instanceof ISkinHolder)) {
            return;
        }
        ISkinHolder inputItem = (ISkinHolder)stackInput.getItem();
        
        ItemStack stackArmour = inputItem.makeStackForEquipment(itemData);
        if (stackArmour == null) {
            return;
        }
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackArmour);
    }
    
    public static CustomEquipmentItemData loadCustomArmourItemDataFromFile(String filename) {
        if (!createArmourDirectory()) {
            return null;
        }
        
        File armourDir = new File(System.getProperty("user.dir"));
        armourDir = new File(armourDir, LibModInfo.ID);
        
        File targetFile = new File(armourDir, File.separatorChar + filename + ".armour");
        
        DataInputStream stream = null;
        CustomEquipmentItemData armourItemData = null;
        
        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(targetFile)));
            armourItemData = new CustomEquipmentItemData(stream);
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Armour file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Armour file load failed.");
            e.printStackTrace();
        } catch (NewerFileVersionException e) {
            ModLogger.log(Level.ERROR, "Can not load custom armour, was saved in newer version.");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return armourItemData;
    }
    
    public static ArrayList<String> getFileNames() {
        ArrayList<String> files = new ArrayList<String>();
        if (!createArmourDirectory()) { return null; }
        
        File armourDir = new File(System.getProperty("user.dir"));
        armourDir = new File(armourDir, LibModInfo.ID);
        
        File[] templateFiles;
        try {
            templateFiles = armourDir.listFiles();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, "Armour file list load failed.");
            e.printStackTrace();
            return null;
        }
        
        for (int i = 0; i < templateFiles.length; i++) {
            if (templateFiles[i].getName().endsWith(".armour")) {
                String cleanName = FilenameUtils.removeExtension(templateFiles[i].getName());
                files.add(cleanName);
            }
        }
        Collections.sort(files);
        return files;
    }
    
    public void setArmourList(ArrayList<String> fileNames) {
        this.serverFileNames = fileNames;
        this.clientFileNames = getFileNames();
    }
    
    public static boolean createArmourDirectory() {
        File armourDir = new File(System.getProperty("user.dir"));
        armourDir = new File(armourDir, LibModInfo.ID);
        if (!armourDir.exists()) {
            try {
                armourDir.mkdir();
            } catch (Exception e) {
                ModLogger.log(Level.WARN, "Unable to create armour directory.");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
