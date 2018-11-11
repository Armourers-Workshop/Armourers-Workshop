package moe.plushie.armourers_workshop.api.common.skin.data;

import moe.plushie.armourers_workshop.api.common.library.ILibraryFile;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

public interface ISkinIdentifier {
    
    public boolean hasLocalId();
    
    public boolean hasLibraryFile();
    
    public boolean hasGlobalId();
    
    public boolean isValid();
    
    public int getSkinLocalId();
    
    public ILibraryFile getSkinLibraryFile();
    
    public int getSkinGlobalId();
    
    public ISkinType getSkinType();
}
