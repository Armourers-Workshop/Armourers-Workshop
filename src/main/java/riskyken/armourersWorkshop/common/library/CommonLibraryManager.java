package riskyken.armourersWorkshop.common.library;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

/**
 * 
 * @author RiskyKen
 *
 */
public class CommonLibraryManager implements ILibraryManager {

    private final LibraryFileList serverPublicFiles;
    private final HashMap<UUID, LibraryFileList> serverPrivateFiles;
    
    public CommonLibraryManager() {
        serverPublicFiles = new LibraryFileList(LibraryFileType.SERVER_PUBLIC);
        serverPrivateFiles = new HashMap<UUID, LibraryFileList>();
    }
    
    @Override
    public void reloadLibrary() {
        long startTime = System.currentTimeMillis();
        ModLogger.log("Loading public library skins");
        int publicFileCount = loadPublicFiles();
        int endTime = (int) (System.currentTimeMillis() - startTime);
        ModLogger.log(String.format("Finished loading %d public library skins in %d ms", publicFileCount, endTime));
        
        ModLogger.log("Loading private library skins");
        startTime = System.currentTimeMillis();
        int privateFileCount = loadPrivateFiles();
        endTime = (int) (System.currentTimeMillis() - startTime);
        ModLogger.log(String.format("Finished loading %d private library skins in %d ms", privateFileCount, endTime));
    }
    
    private int loadPublicFiles() {
        File directory = SkinIOUtils.getSkinLibraryDirectory();
        ArrayList<LibraryFile> fileList = LibraryHelper.getSkinFilesInDirectory(directory);
        setFileList(fileList, LibraryFileType.SERVER_PUBLIC);
        return fileList.size();
    }
    
    private int loadPrivateFiles() {
        int count = 0;
        File directory = SkinIOUtils.getSkinLibraryDirectory();
        directory = new File(directory, "private");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File files[] = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                try {
                    UUID playerId = UUID.fromString(file.getName());
                    LibraryFileList fileList = new LibraryFileList(LibraryFileType.SERVER_PRIVATE);
                    ArrayList<LibraryFile> privateFileList = LibraryHelper.getSkinFilesInDirectory(file);
                    fileList.setFileList(privateFileList);
                    serverPrivateFiles.put(playerId, fileList);
                    count += fileList.getFileCount();
                } catch(IllegalArgumentException e) {
                }
            }
        }
        return count;
    }
    
    @Override
    public LibraryFileList getClientPublicFileList() {
        ModLogger.log(Level.WARN, "Tried to get client file list from the server library manager.");
        return null;
    }
    
    @Override
    public LibraryFileList getServerPublicFileList() {
        return serverPublicFiles;
    }
    
    @Override
    public LibraryFileList getServerPrivateFileList(EntityPlayer player) {
        return serverPrivateFiles.get(player.getUniqueID());
    }
    
    @Override
    public void setFileList(ArrayList<LibraryFile> fileList, LibraryFileType listType) {
        switch (listType) {
        case LOCAL:
            ModLogger.log(Level.WARN, "Tried to set the file list in the server library manager.");
            break;
        case SERVER_PUBLIC:
            serverPublicFiles.setFileList(fileList);
            break;
        case SERVER_PRIVATE:
            //serverPrivateFiles.setFileList(fileList);
            break;
        }
    }
    
    @Override
    public void addFileToListType(LibraryFile file, LibraryFileType listType, EntityPlayer player) {
        switch (listType) {
        case LOCAL:
            ModLogger.log(Level.WARN, "Tried to add a file in the server library manager.");
            break;
        case SERVER_PUBLIC:
            serverPublicFiles.addFileToList(file);
            break;
        case SERVER_PRIVATE:
            LibraryFileList fileList = serverPrivateFiles.get(player.getUniqueID());
            if (fileList != null) {
                fileList.addFileToList(file);
            } else {
                fileList = new LibraryFileList(LibraryFileType.SERVER_PRIVATE);
                fileList.addFileToList(file);
                serverPrivateFiles.put(player.getUniqueID(), fileList);
            }
            break;
        }
    }
    
    @Override
    public void removeFileFromListType(LibraryFile file, LibraryFileType listType, EntityPlayer player) {
        switch (listType) {
        case LOCAL:
            ModLogger.log(Level.WARN, "Tried to remove a file in the server library manager.");
            break;
        case SERVER_PUBLIC:
            serverPublicFiles.removeFileFromList(file);
            break;
        case SERVER_PRIVATE:
            LibraryFileList fileList = serverPrivateFiles.get(player.getUniqueID());
            if (fileList != null) {
                fileList.removeFileFromList(file);
            } else {
                fileList = new LibraryFileList(LibraryFileType.SERVER_PRIVATE);
                fileList.removeFileFromList(file);
                serverPrivateFiles.put(player.getUniqueID(), fileList);
            }
            break;
        }
    }
    
    @Override
    public void syncLibraryWithPlayer(EntityPlayerMP player) {
        serverPublicFiles.syncFileListWithPlayer(player);
        LibraryFileList privateList = serverPrivateFiles.get(player.getUniqueID());
        if (privateList != null) {
            privateList.syncFileListWithPlayer(player);
        }
    }
}
