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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkin;
import riskyken.armourersWorkshop.common.items.ItemEquipmentSkinTemplate;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.ModLogger;

public class TileEntityArmourLibrary extends AbstractTileEntityInventory {
    
    public ArrayList<String> fileNames = null;
    
    public TileEntityArmourLibrary() {
        this.items = new ItemStack[2];
    }
    
    @Override
    public String getInventoryName() {
        return LibBlockNames.ARMOUR_LIBRARY;
    }

    public void saveArmour(String filename, EntityPlayerMP player) {
        //Check we have a valid item to save from.
        ItemStack stackInput = getStackInSlot(0);
        if (stackInput == null) { return; }
        if (!(stackInput.getItem() instanceof ItemEquipmentSkin)) { return; }
        if (!stackInput.hasTagCompound()) { return; };
        NBTTagCompound itemNBT = stackInput.getTagCompound();
        if (!itemNBT.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) { return; }
        NBTTagCompound dataNBT = itemNBT.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
        
        
        if (!createArmourDirectory()) { return; }

        File armourDir = new File(System.getProperty("user.dir"));
        armourDir = new File(armourDir, LibModInfo.ID);
        
        DataOutputStream stream;
        File targetFile = new File(armourDir, File.separatorChar + filename + ".armour");
        
        CustomArmourItemData customArmourItemData = new CustomArmourItemData(dataNBT);
        
        try {
            stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)));
            customArmourItemData.writeToStream(stream);
            stream.flush();
            stream.close();
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Armour file not found.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Armour file save failed.");
            e.printStackTrace();
            return;
        }
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackInput);
    }
    
    public void loadArmour(String filename, EntityPlayerMP player) {
        //Check we have a valid item to load from.
        ItemStack stackInput = getStackInSlot(0);
        if (stackInput == null) { return; }
        if (!(stackInput.getItem() instanceof ItemEquipmentSkinTemplate)) { return; }
        
        if (!createArmourDirectory()) { return; }
        
        File armourDir = new File(System.getProperty("user.dir"));
        armourDir = new File(armourDir, LibModInfo.ID);
        
        DataInputStream stream;
        File targetFile = new File(armourDir, File.separatorChar + filename + ".armour");
        
        CustomArmourItemData armourItemData;
        
        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(targetFile)));
            armourItemData = new CustomArmourItemData(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Armour file not found.");
            e.printStackTrace();
            return;
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Armour file load failed.");
            e.printStackTrace();
            return;
        }
        
        ModLogger.log(armourItemData.getType().ordinal());
        ItemStack stackOutput = new ItemStack(ModItems.equipmentSkin, 1, armourItemData.getType().ordinal() - 1);
        
        NBTTagCompound itemNBT = new NBTTagCompound();
        NBTTagCompound armourNBT = new NBTTagCompound();
        
        armourItemData.writeToNBT(armourNBT);
        itemNBT.setTag(LibCommonTags.TAG_ARMOUR_DATA, armourNBT);
        
        stackOutput.setTagCompound(itemNBT);
        
        this.decrStackSize(0, 1);
        this.setInventorySlotContents(1, stackOutput);
    }
    
    public ArrayList<String> getFileNames() {
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
        
        return files;
    }
    
    public void setArmourList(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
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
