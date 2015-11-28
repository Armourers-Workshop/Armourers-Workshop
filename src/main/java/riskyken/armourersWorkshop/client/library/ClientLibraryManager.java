package riskyken.armourersWorkshop.client.library;

import java.io.File;
import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.common.library.ILibraryManager;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileList;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.common.library.LibraryHelper;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

@SideOnly(Side.CLIENT)
public class ClientLibraryManager implements ILibraryManager {

    private final LibraryFileList serverPublicFiles;
    private final LibraryFileList serverPrivateFiles;
    private final LibraryFileList clientFiles;
    
    public ClientLibraryManager() {
        serverPublicFiles = new LibraryFileList(LibraryFileType.SERVER_PUBLIC, this);
        serverPrivateFiles = new LibraryFileList(LibraryFileType.SERVER_PRIVATE, this);
        clientFiles = new LibraryFileList(LibraryFileType.LOCAL, this);
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onClientDisconnected(ClientDisconnectionFromServerEvent event) {
        //NOTE Called from Netty client IO thread.
        serverPublicFiles.clearList();
        serverPrivateFiles.clearList();
    }
    
    @Override
    public void reloadLibrary() {
        long startTime = System.currentTimeMillis();
        ModLogger.log("Loading library skins");
        File directory = SkinIOUtils.getSkinLibraryDirectory();
        ArrayList<LibraryFile> fileList = LibraryHelper.getSkinFilesInDirectory(directory);
        setFileList(fileList, LibraryFileType.LOCAL);
        ModLogger.log(String.format("Finished loading %d library skins in %d ms", clientFiles.getFileCount(), System.currentTimeMillis() - startTime));
    }
    
    @Override
    public LibraryFileList getClientPublicFileList() {
        return clientFiles;
    }
    
    @Override
    public LibraryFileList getServerPublicFileList() {
        return serverPublicFiles;
    }
    
    @Override
    public LibraryFileList getServerPrivateFileList(EntityPlayerMP player) {
        return serverPrivateFiles;
    }

    @Override
    public void setFileList(ArrayList<LibraryFile> fileList, LibraryFileType listType) {
        switch (listType) {
        case LOCAL:
            clientFiles.setFileList(fileList);
            break;
        case SERVER_PUBLIC:
            serverPublicFiles.setFileList(fileList);
            break;
        case SERVER_PRIVATE:
            serverPrivateFiles.setFileList(fileList);
            break;
        }
    }
    
    @Override
    public void markFileListDirty(LibraryFileType listType) {
        switch (listType) {
        case LOCAL:
            clientFiles.markDirty();
            break;
        case SERVER_PUBLIC:
            serverPublicFiles.markDirty();
            break;
        case SERVER_PRIVATE:
            serverPrivateFiles.markDirty();
            break;
        }
    }
    
    @Override
    public void addFileToListType(LibraryFile file, LibraryFileType listType) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void removeFileFromListType(LibraryFile file, LibraryFileType listType) {
        // TODO Auto-generated method stub
    }
    
    @Override
    public void requestNewFileList(LibraryFileType listType) {
        // TODO Send message to server asking for new file list.
    }
}
