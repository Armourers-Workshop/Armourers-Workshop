package riskyken.armourersWorkshop.api.common.skin.data;

import riskyken.armourersWorkshop.common.library.LibraryFile;

public interface ISkinIdentifier {
    
    public int getSkinLocalId();
    
    public LibraryFile getSkinLibraryFile();
    
    public int getSkinGlobalId();
}
