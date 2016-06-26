package riskyken.armourersWorkshop.client.library;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
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
        serverPublicFiles = new LibraryFileList(LibraryFileType.SERVER_PUBLIC);
        serverPrivateFiles = new LibraryFileList(LibraryFileType.SERVER_PRIVATE);
        clientFiles = new LibraryFileList(LibraryFileType.LOCAL);
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
}
