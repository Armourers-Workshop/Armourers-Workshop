package moe.plushie.armourers_workshop.common.library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerLibraryFileList;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class LibraryFileList {

    private final ArrayList<LibraryFile> fileList;
    private final HashMap<String, ArrayList<LibraryFile>> typeListsMap;
    private final LibraryFileType listType;
    /** Players that have an up to date copy of the servers library files. */
    private final HashSet<UUID> syncedClients;

    public LibraryFileList(LibraryFileType listType) {
        this.fileList = new ArrayList<LibraryFile>();
        this.typeListsMap = new HashMap<String, ArrayList<LibraryFile>>();
        this.listType = listType;
        this.syncedClients = new HashSet<UUID>();
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
        updateTypeLists();
    }

    public ArrayList<LibraryFile> getFileList() {
        ArrayList<LibraryFile> returnList = new ArrayList<LibraryFile>();
        synchronized (this.fileList) {
            // ModLogger.log(String.format("Getting list with %d files from list type %s",
            // this.fileList.size(), this.listType.toString()));
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

    private void updateTypeLists() {
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        synchronized (typeListsMap) {
            typeListsMap.clear();
            for (int i = 0; i < skinTypes.size(); i++) {
                ISkinType skinType = skinTypes.get(i);
                ArrayList<LibraryFile> typeList = getFileListForSkinType(skinType);
                typeListsMap.put(skinType.getRegistryName(), typeList);
            }
            ModLogger.log(String.format("Created %d type lists for file list type %s.", typeListsMap.size(), this.listType.toString()));
        }
    }

    private ArrayList<LibraryFile> getFileListForSkinType(ISkinType skinType) {
        ArrayList<LibraryFile> typeList = new ArrayList<LibraryFile>();
        synchronized (this.fileList) {
            for (int i = 0; i < this.fileList.size(); i++) {
                LibraryFile libraryFile = this.fileList.get(i);
                if (libraryFile.skinType == skinType) {
                    typeList.add(libraryFile);
                }
            }
        }
        return typeList;
    }

    public ArrayList<LibraryFile> getCachedFileListForSkinType(ISkinType skinType) {
        synchronized (typeListsMap) {
            return typeListsMap.get(skinType.getRegistryName());
        }
    }

    public void removeFileFromList(LibraryFile file) {
        synchronized (this.fileList) {
            for (int i = 0; i < this.fileList.size(); i++) {
                if (this.fileList.get(i).getFullName().equals(file.getFullName())) {
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
            if (!syncedClients.contains(player.getUniqueID())) {
                syncedClients.add(player.getUniqueID());
                ArrayList<LibraryFile> fileList = getFileList();
                ModLogger.log("Syncing library to " + player.getName() + ".");
                ModLogger.log(String.format("Sending file list type %s to %s", listType.toString(), player.getName()));
                MessageServerLibraryFileList message = new MessageServerLibraryFileList(fileList, this.listType);
                PacketHandler.networkWrapper.sendTo(message, player);
            }
        }
    }
}
