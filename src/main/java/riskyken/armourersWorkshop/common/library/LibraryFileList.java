package riskyken.armourersWorkshop.common.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerLibraryFileList;
import riskyken.armourersWorkshop.utils.ModLogger;

public class LibraryFileList {

    private final ArrayList<LibraryFile> fileList;
    private final LibraryFileType listType;
    /** Players that have an up to date copy of the servers library files. */
    private final HashSet<UUID> syncedClients;
    
    public LibraryFileList(LibraryFileType listType) {
        this.fileList = new ArrayList<LibraryFile>();
        this.listType = listType;
        syncedClients = new HashSet<UUID>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onClientDisconnected(PlayerLoggedOutEvent event) {
        synchronized (syncedClients) {
            syncedClients.remove(event.player.getUniqueID());
        }
    }
    
    public void markDirty() {
        synchronized (syncedClients) {
            syncedClients.clear();
        }
    }
    
    public ArrayList<LibraryFile> getFileList() {
        ArrayList<LibraryFile> returnList = new ArrayList<LibraryFile>();
        synchronized (this.fileList) {
            //ModLogger.log(String.format("Getting list with %d files from list type %s", this.fileList.size(), this.listType.toString()));
            returnList.addAll(this.fileList);
        }
        return returnList;
    }
    
    public void setFileList(ArrayList<LibraryFile> fileList) {
        synchronized (this.fileList) {
            this.fileList.clear();
            this.fileList.addAll(fileList);
        }
        markDirty();
    }
    
    public void addFileToList(LibraryFile file) {
        removeFileFromList(file);
        synchronized (this.fileList) {
            this.fileList.add(file);
            Collections.sort(this.fileList);
        }
        markDirty();
    }
    
    public void removeFileFromList(LibraryFile file) {
        synchronized (this.fileList) {
            for (int i = 0; i < this.fileList.size(); i++) {
                if (this.fileList.get(i).fileName.equals(file.fileName)) {
                    this.fileList.remove(i);
                    markDirty();
                    break;
                }
            }
        }
    }
    
    public int getFileCount() {
        int size = 0;
        synchronized (this.fileList) {
            size = this.fileList.size();
        }
        return size;
    }
    
    public void clearList() {
        synchronized (this.fileList) {
            this.fileList.clear();
        }
        markDirty();
    }
    
    public void syncFileListWithPlayer(EntityPlayerMP player) {
        synchronized (this.syncedClients) {
            ArrayList<LibraryFile> fileList = getFileList();
            if (!syncedClients.contains(player.getUniqueID())) {
                syncedClients.add(player.getUniqueID());
                ModLogger.log(String.format("Sending file list type %s to %s", listType.toString(), player.getDisplayName()));
                MessageServerLibraryFileList message = new MessageServerLibraryFileList(fileList, this.listType);
                PacketHandler.networkWrapper.sendTo(message, player);
            }
        }
    }
}
