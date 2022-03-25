package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.common.library.ILibraryFile;

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
