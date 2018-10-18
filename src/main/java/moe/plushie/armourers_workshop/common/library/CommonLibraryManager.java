package moe.plushie.armourers_workshop.common.library;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * 
 * @author RiskyKen
 *
 */
public class CommonLibraryManager implements ILibraryManager {

    private final LibraryFileList serverPublicFiles;
    private final HashMap<UUID, LibraryFileList> serverPrivateFiles;
    private boolean loadingLibaray;
    
    public CommonLibraryManager() {
        serverPublicFiles = new LibraryFileList(LibraryFileType.SERVER_PUBLIC);
        serverPrivateFiles = new HashMap<UUID, LibraryFileList>();
        loadingLibaray = false;
    }
    
    @Override
    public void reloadLibrary() {
        reloadLibrary(null);
    }
    
    @Override
    public void reloadLibrary(ILibraryCallback callback) {
        if (!loadingLibaray) {
            loadingLibaray = true;
            (new Thread(new LibraryLoader(this, callback),LibModInfo.NAME + " library thread.")).start();
        } else {
            ModLogger.log("Library is already loading.");
        }
    }
    
    private void finishedLoading() {
        loadingLibaray = false;
    }
    
    private int loadPublicFiles() {
        File directory = ArmourersWorkshop.getProxy().getSkinLibraryDirectory();
        ArrayList<LibraryFile> fileList = LibraryHelper.getSkinFilesInDirectory(directory, true);
        setFileList(fileList, LibraryFileType.SERVER_PUBLIC);
        return fileList.size();
    }
    
    private int loadPrivateFiles() {
        int count = 0;
        File directory = ArmourersWorkshop.getProxy().getSkinLibraryDirectory();
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
                    ArrayList<LibraryFile> privateFileList = LibraryHelper.getSkinFilesInDirectory(file, true);
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
    
    private static class LibraryLoader implements Runnable {

        private CommonLibraryManager libraryManager;
        private ILibraryCallback callback;
        
        public LibraryLoader(CommonLibraryManager libraryManager, ILibraryCallback callback) {
            this.libraryManager = libraryManager;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            ModLogger.log("Loading public library skins");
            int publicFileCount = libraryManager.loadPublicFiles();
            int endTime = (int) (System.currentTimeMillis() - startTime);
            ModLogger.log(String.format("Finished loading %d server public library skins in %d ms", publicFileCount, endTime));
            
            ModLogger.log("Loading private library skins");
            startTime = System.currentTimeMillis();
            int privateFileCount = libraryManager.loadPrivateFiles();
            endTime = (int) (System.currentTimeMillis() - startTime);
            ModLogger.log(String.format("Finished loading %d server private library skins in %d ms", privateFileCount, endTime));
            libraryManager.finishedLoading();
            if (callback != null) {
                callback.libraryReloaded(libraryManager);
            }
        }
    }
}
