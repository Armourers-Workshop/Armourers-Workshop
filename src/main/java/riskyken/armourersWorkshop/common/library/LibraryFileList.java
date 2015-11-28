package riskyken.armourersWorkshop.common.library;

import java.util.ArrayList;

public class LibraryFileList {

    private final ArrayList<LibraryFile> fileList;
    private final LibraryFileType listType;
    private final ILibraryManager libraryMananger;
    private boolean isDirty;
    private boolean haveRequested;
    
    public LibraryFileList(LibraryFileType listType, ILibraryManager libraryManager) {
        fileList = new ArrayList<LibraryFile>();
        this.listType = listType;
        this.libraryMananger = libraryManager;
        isDirty = true;
    }
    
    public void markDirty() {
        isDirty = true;
    }
    
    public ArrayList<LibraryFile> getFileList() {
        if (isDirty) {
            requestNewFileList();
        }
        return fileList;
    }
    
    private void requestNewFileList() {
        if (!haveRequested) {
            haveRequested = true;
            libraryMananger.requestNewFileList(listType);
        }
    }
    
    public void setFileList(ArrayList<LibraryFile> fileList) {
        isDirty = false;
        this.fileList.clear();
        this.fileList.addAll(fileList);
    }
    
    public int getFileCount() {
        return fileList.size();
    }
    
    public void clearList() {
        fileList.clear();
    }
}
