package riskyken.armourersWorkshop.api.common.skin.data;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.library.LibraryFile;

public interface ISkinIdentifier {
    
    public int getSkinLocalId();
    
    public LibraryFile getSkinLibraryFile();
    
    public int getSkinGlobalId();
    
    public ISkinType getSkinType();
}
