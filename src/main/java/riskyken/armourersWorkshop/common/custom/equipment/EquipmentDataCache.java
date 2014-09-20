package riskyken.armourersWorkshop.common.custom.equipment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageServerSendEquipmentData;
import riskyken.armourersWorkshop.utils.ModLogger;

/**
 * Holds a cache of equipment data on the server that will be sent to clients if
 * they request it.
 * 
 * @author RiskyKen
 *
 */
public final class EquipmentDataCache {
    
    private static HashMap<Integer, CustomArmourItemData> equipmentDataCache = new HashMap<Integer, CustomArmourItemData>();

    
    public static void addEquipmentDataToCache(CustomArmourItemData equipmentData) {
        addEquipmentDataToCache(equipmentData, equipmentData.hashCode());
    }
    
    public static void addEquipmentDataToCache(CustomArmourItemData equipmentData, int equipmentId) {
        if (!equipmentDataCache.containsKey(equipmentId)) {
            equipmentDataCache.put(equipmentId, equipmentData);
            if (!haveEquipmentOnDisk(equipmentId)) {
                saveEquipmentToDisk(equipmentData);
            }
        }
    }
    
    public static CustomArmourItemData getEquipmentData(int equipmentId) {
        if (equipmentDataCache.containsKey(equipmentId)) {
            return equipmentDataCache.get(equipmentId);
        }
        return null;
    }
    
    public static void clientRequestEquipmentData(int equipmentId, EntityPlayerMP player) {
        ModLogger.log("player: " + player + " equipmentId: " + equipmentId);
        
        if (!equipmentDataCache.containsKey(equipmentId)) {
            if (haveEquipmentOnDisk(equipmentId)) {
                CustomArmourItemData equipmentData;
                equipmentData = loadEquipmentFromDisk(equipmentId);
                addEquipmentDataToCache(equipmentData, equipmentId);
            }
        }
        
        if (equipmentDataCache.containsKey(equipmentId)) {
            CustomArmourItemData equpmentData = equipmentDataCache.get(equipmentId);
            PacketHandler.networkWrapper.sendTo(new MessageServerSendEquipmentData(equpmentData), player);
        }
    }
    
    private static boolean haveEquipmentOnDisk(int equipmentId) {
        createEquipmentDirectory();
        File equipmentDir = new File(System.getProperty("user.dir"));
        equipmentDir = new File(equipmentDir, "equipment-database");
        
        File targetFile = new File(equipmentDir, File.separatorChar + String.valueOf(equipmentId));
        return targetFile.exists();
    }
    
    private static void saveEquipmentToDisk(CustomArmourItemData equipmentData) {
        createEquipmentDirectory();
        File equipmentDir = new File(System.getProperty("user.dir"));
        equipmentDir = new File(equipmentDir, "equipment-database");
        
        File targetFile = new File(equipmentDir, File.separatorChar + String.valueOf(equipmentData.hashCode()));
        
        DataOutputStream stream = null;
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
    }
    
    private static CustomArmourItemData loadEquipmentFromDisk(int equipmentId) {
        createEquipmentDirectory();
        File equipmentDir = new File(System.getProperty("user.dir"));
        equipmentDir = new File(equipmentDir, "equipment-database");
        
        File targetFile = new File(equipmentDir, File.separatorChar + String.valueOf(equipmentId));
        if (!targetFile.exists()) {
            return null;
        }
        
        CustomArmourItemData equipmentData;
        DataInputStream stream = null;
        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(targetFile)));
            equipmentData = new CustomArmourItemData(stream);
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Armour file not found.");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Armour file load failed.");
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(stream);
        }
        
        return equipmentData;
    }
    
    private static boolean createEquipmentDirectory() {
        File equipmentDir = new File(System.getProperty("user.dir"));
        equipmentDir = new File(equipmentDir, "equipment-database");
        
        if (!equipmentDir.exists()) {
            try {
                equipmentDir.mkdir();
            } catch (Exception e) {
                ModLogger.log(Level.WARN, "Unable to create equipment directory.");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
