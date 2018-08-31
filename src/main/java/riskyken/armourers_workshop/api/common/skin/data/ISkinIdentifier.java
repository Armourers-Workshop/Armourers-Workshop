package riskyken.armourers_workshop.api.common.skin.data;

import riskyken.armourers_workshop.api.common.library.ILibraryFile;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;

public interface ISkinIdentifier {
    
    public boolean hasLocalId();
    
    public boolean hasLibraryFile();
    
    public boolean hasGlobalId();
    
    public int getSkinLocalId();
    
    public ILibraryFile getSkinLibraryFile();
    
    public int getSkinGlobalId();
    
    public ISkinType getSkinType();
}
