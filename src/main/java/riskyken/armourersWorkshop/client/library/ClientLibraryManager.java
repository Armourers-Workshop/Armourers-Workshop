package riskyken.armourersWorkshop.client.library;

import java.io.File;
import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.library.ILibraryCallback;
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
    private boolean loadingLibaray;
    
    public ClientLibraryManager() {
        serverPublicFiles = new LibraryFileList(LibraryFileType.SERVER_PUBLIC);
        serverPrivateFiles = new LibraryFileList(LibraryFileType.SERVER_PRIVATE);
        clientFiles = new LibraryFileList(LibraryFileType.LOCAL);
        loadingLibaray = false;
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
        reloadLibrary(null);
    }
    
    @Override
    public void reloadLibrary(ILibraryCallback callback) {
        if (!loadingLibaray) {
            loadingLibaray = true;
            (new Thread(new LibraryLoader(this, callback),LibModInfo.NAME + " library thread.")).start();
        } else {
            ModLogger.log("Library is already loading client.");
        }
    }
    
    private void finishedLoading() {
        loadingLibaray = false;
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
    public LibraryFileList getServerPrivateFileList(EntityPlayer player) {
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
    public void addFileToListType(LibraryFile file, LibraryFileType listType, EntityPlayer player) {
        switch (listType) {
        case LOCAL:
            clientFiles.addFileToList(file);
            break;
        case SERVER_PUBLIC:
            serverPublicFiles.addFileToList(file);
            break;
        case SERVER_PRIVATE:
            serverPrivateFiles.addFileToList(file);
            break;
        }
    }
    
    @Override
    public void removeFileFromListType(LibraryFile file, LibraryFileType listType, EntityPlayer player) {
        switch (listType) {
        case LOCAL:
            clientFiles.removeFileFromList(file);
            break;
        case SERVER_PUBLIC:
            serverPublicFiles.removeFileFromList(file);
            break;
        case SERVER_PRIVATE:
            serverPrivateFiles.removeFileFromList(file);
            break;
        }
    }
    
    @Override
    public void syncLibraryWithPlayer(EntityPlayerMP player) {
        // TODO Check if this is ever called on the client.
        // Maybe used on LAN servers?
    }
    
    private static class LibraryLoader implements Runnable {

        private ClientLibraryManager libraryManager;
        private ILibraryCallback callback;
        
        public LibraryLoader(ClientLibraryManager libraryManager, ILibraryCallback callback) {
            this.libraryManager = libraryManager;
            this.callback = callback;
        }
        
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            ModLogger.log("Loading library skins");
            File directory = SkinIOUtils.getSkinLibraryDirectory();
            ArrayList<LibraryFile> fileList = LibraryHelper.getSkinFilesInDirectory(directory, true);
            libraryManager.setFileList(fileList, LibraryFileType.LOCAL);
            ModLogger.log(String.format("Finished loading %d client library skins in %d ms", libraryManager.clientFiles.getFileCount(), System.currentTimeMillis() - startTime));
            libraryManager.finishedLoading();
            if (callback != null) {
                callback.libraryReloaded(libraryManager);
            }
        }
    }
}
