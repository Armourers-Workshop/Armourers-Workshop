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
        fileList = new ArrayList<LibraryFile>();
        this.listType = listType;
        syncedClients = new HashSet<UUID>();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onClientDisconnected(PlayerLoggedOutEvent event) {
        syncedClients.remove(event.player.getUniqueID());
    }
    
    public void markDirty() {
        syncedClients.clear();
    }
    
    public ArrayList<LibraryFile> getFileList() {
        ArrayList<LibraryFile> returnList = new ArrayList<LibraryFile>();
        returnList.addAll(fileList);
        return returnList;
    }
    
    public void setFileList(ArrayList<LibraryFile> fileList) {
        this.fileList.clear();
        this.fileList.addAll(fileList);
        markDirty();
    }
    
    public void addFileToList(LibraryFile file) {
        removeFileFromList(file);
        fileList.add(file);
        Collections.sort(fileList);
        markDirty();
    }
    
    public void removeFileFromList(LibraryFile file) {
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).fileName.equals(file.fileName)) {
                fileList.remove(i);
                markDirty();
                break;
            }
        }
    }
    
    public int getFileCount() {
        return fileList.size();
    }
    
    public void clearList() {
        fileList.clear();
        markDirty();
    }
    
    public void syncFileListWithPlayer(EntityPlayerMP player) {
        if (!syncedClients.contains(player.getUniqueID())) {
            syncedClients.add(player.getUniqueID());
            ModLogger.log(String.format("Sending file list type %s to %s", listType.toString(), player.getDisplayName()));
            MessageServerLibraryFileList message = new MessageServerLibraryFileList(fileList, listType);
            PacketHandler.networkWrapper.sendTo(message, player);
        }
    }
}
