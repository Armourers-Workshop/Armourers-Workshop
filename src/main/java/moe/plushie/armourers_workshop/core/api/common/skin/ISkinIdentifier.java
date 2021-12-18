package moe.plushie.armourers_workshop.core.api.common.skin;

import moe.plushie.armourers_workshop.core.api.common.library.ILibraryFile;

public interface ISkinIdentifier {
    
    public boolean hasLocalId();
    
    public boolean hasLibraryFile();
    
    public boolean hasGlobalId();
    
    public boolean isValid();
    
    public int getSkinLocalId();
    
    public ILibraryFile getSkinLibraryFile();
    
    public int getSkinGlobalId();
    
    public ISkinType getType();
}
