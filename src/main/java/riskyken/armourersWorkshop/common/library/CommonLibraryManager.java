package riskyken.armourersWorkshop.common.library;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;


public class CommonLibraryManager implements ILibraryManager {

    //Filename link
    //Keep a list of types
    private final LibraryFileList serverPublicFiles;
    private final HashMap<UUID, LibraryFileList> serverPrivateFiles;
    
    public CommonLibraryManager() {
        serverPublicFiles = new LibraryFileList(LibraryFileType.SERVER_PUBLIC, this);
        serverPrivateFiles = new HashMap<UUID, LibraryFileList>();
    }
    
    @Override
    public void reloadLibrary() {
        long startTime = System.currentTimeMillis();
        ModLogger.log("Loading library public skins");
        File directory = SkinIOUtils.getSkinLibraryDirectory();
        ArrayList<LibraryFile> fileList = LibraryHelper.getSkinFilesInDirectory(directory);
        setFileList(fileList, LibraryFileType.SERVER_PUBLIC);
        
        
        int endTime = (int) (System.currentTimeMillis() - startTime);
        ModLogger.log(String.format("Finished loading %d library skins in %d ms", serverPublicFiles.getFileCount(), endTime));
    }
    
    @Override
    public LibraryFileList getClientPublicFileList() {
        return null;
    }
    
    @Override
    public LibraryFileList getServerPublicFileList() {
        return serverPublicFiles;
    }
    
    @Override
    public LibraryFileList getServerPrivateFileList(EntityPlayerMP player) {
        //TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void setFileList(ArrayList<LibraryFile> fileList, LibraryFileType listType) {
        switch (listType) {
        case LOCAL:
            ModLogger.log(Level.WARN, "Tried to set the file list in the server library manager.");
            break;
        case SERVER_PUBLIC:
            serverPublicFiles.setFileList(fileList);
            serverPublicFiles.markDirty();
            break;
        case SERVER_PRIVATE:
            //serverPrivateFiles.setFileList(fileList);
            break;
        }
    }
    
    @Override
    public void markFileListDirty(LibraryFileType listType) {
        //TODO Auto-generated method stub
    }
    
    @Override
    public void addFileToListType(LibraryFile file, LibraryFileType listType) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void removeFileFromListType(LibraryFile file,
            LibraryFileType listType) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void requestNewFileList(LibraryFileType listType) {
        // TODO Auto-generated method stub
        
    }
}
