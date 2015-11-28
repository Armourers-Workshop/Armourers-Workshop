package riskyken.armourersWorkshop.common.library;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ILibraryManager {
    
    public void reloadLibrary();
    
    public LibraryFileList getClientPublicFileList();
    
    public LibraryFileList getServerPublicFileList();
    
    public LibraryFileList getServerPrivateFileList(EntityPlayerMP player);
    
    public void setFileList(ArrayList<LibraryFile> fileList, LibraryFileType listType);
    
    public void markFileListDirty(LibraryFileType listType);
    
    public void addFileToListType(LibraryFile file, LibraryFileType listType);
    
    public void removeFileFromListType(LibraryFile file, LibraryFileType listType);

    public void requestNewFileList(LibraryFileType listType);
}
