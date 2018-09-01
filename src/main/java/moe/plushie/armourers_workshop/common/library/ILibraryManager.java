package moe.plushie.armourers_workshop.common.library;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public interface ILibraryManager {
    
    public void reloadLibrary();
    
    public void reloadLibrary(ILibraryCallback callback);
    
    public LibraryFileList getClientPublicFileList();
    
    public LibraryFileList getServerPublicFileList();
    
    public LibraryFileList getServerPrivateFileList(EntityPlayer player);
    
    public void setFileList(ArrayList<LibraryFile> fileList, LibraryFileType listType);
    
    public void addFileToListType(LibraryFile file, LibraryFileType listType, EntityPlayer player);
    
    public void removeFileFromListType(LibraryFile file, LibraryFileType listType, EntityPlayer player);
    
    public void syncLibraryWithPlayer(EntityPlayerMP player);
}
