package riskyken.armourersWorkshop.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import net.minecraftforge.common.DimensionManager;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.exception.NewerFileVersionException;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;

public final class SkinIOUtils {
    
    public static boolean saveSkinFromFileName(String fileName, Skin skin) {
        File file = new File(getSkinLibraryDirectory(), fileName);
        return saveSkinToFile(file, skin);
    }
    
    public static boolean saveSkinToFile(File file, Skin skin) {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdir();
        }
        DataOutputStream stream = null;
        
        try {
            stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            skin.writeToStream(stream);
            stream.flush();
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Skin file not found.");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Skin file save failed.");
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.closeQuietly(stream);
        }
        
        return true;
    }
    
    public static Skin loadSkinFromFileName(String fileName) {
        File file = new File(getSkinLibraryDirectory(), fileName);
        return loadSkinFromFile(file);
    }
    
    public static Skin loadSkinFromFile(File file) {
        DataInputStream stream = null;
        Skin skin = null;
        
        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            skin = new Skin(stream);
        } catch (FileNotFoundException e) {
            ModLogger.log(Level.WARN, "Skin file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            ModLogger.log(Level.ERROR, "Skin file load failed.");
            e.printStackTrace();
        } catch (NewerFileVersionException e) {
            ModLogger.log(Level.ERROR, "Can not load skin file it was saved in newer version.");
            e.printStackTrace();
        } catch (InvalidCubeTypeException e) {
            ModLogger.log(Level.ERROR, "Unable to load skin. Unknown cube types found.");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
        
        return skin;
    }
    
    public static String getSkinTypeNameFromFile(File file) {
        DataInputStream stream = null;
        Skin skin = null;
        String skinTypeName = "";
        
        try {
            stream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            skinTypeName = Skin.readSkinTypeNameFromStream(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NewerFileVersionException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
    
        return skinTypeName;
    }
    
    public static void makeDatabaseDirectory() {
        File directory = getSkinDatabaseDirectory();
        ModLogger.log("Loading skin database at: " + directory.getAbsolutePath());
        if (!directory.exists()) {
            if (directory.mkdir()) {
                copyOldDatabase();
            }
        }
    }
    
    public static void makeLibraryDirectory() {
        File directory = getSkinLibraryDirectory();
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
    
    public static void copyOldDatabase() {
        ModLogger.log("Moving skin database to a new location.");
        
        File dirNewDatabase = getSkinDatabaseDirectory();
        File dirOldDatabase = getOldSkinDatabaseDirectory();
        if (!dirOldDatabase.exists()) {
            ModLogger.log("Old database not found.");
            return;
        }
        
        File[] oldFiles = dirOldDatabase.listFiles();
        for (int i = 0; i < oldFiles.length; i++) {
            File oldFile = oldFiles[i];
            ModLogger.log("Copying file: " + oldFile.getName());
            File newFile = new File(dirNewDatabase, oldFile.getName());
            try {
                FileUtils.copyFile(oldFile, newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static File getSkinDatabaseDirectory() {
        return new File(DimensionManager.getCurrentSaveRootDirectory(), "skin-database");
    }
    
    public static File getOldSkinDatabaseDirectory() {
        return new File(System.getProperty("user.dir"), "equipment-database");
    }
    
    public static File getSkinLibraryDirectory() {
        return new File(System.getProperty("user.dir"), LibModInfo.ID);
    }
    
    public static boolean createDirectory(File file) {
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }
}
