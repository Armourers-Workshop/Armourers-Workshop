package moe.plushie.armourers_workshop.common.skin.data;

import moe.plushie.armourers_workshop.api.common.library.ILibraryFile;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

public class SkinIdentifier implements ISkinIdentifier {
    
    private static final String TAG_SKIN_ID_DATA = "identifier";
    private static final String TAG_SKIN_LOCAL_ID = "localId";
    private static final String TAG_SKIN_LIBRARY_FILE = "libraryFile";
    private static final String TAG_SKIN_GLOBAL_ID = "globalId";
    private static final String TAG_SKIN_TYPE = "skinType";
    
    private static final String TAG_SKIN_OLD_ID = "skinId";
    
    private int localId;
    private ILibraryFile libraryFile;
    private int globalId;
    private ISkinType skinType;
    
    
    // 1 2 3
    // 1 3 2
    // 2 1 3
    // 2 3 1
    // 3 1 2
    // 3 2 1
    
    // 6 Permutations
    // primary secondary tertiary
    
    public SkinIdentifier() {
        this.localId = 0;
        this.libraryFile = null;
        this.globalId = 0;
        this.skinType = null;
    }
    
    public SkinIdentifier(int localId, ILibraryFile libraryFile, int globalId, ISkinType skinType) {
        this.localId = localId;
        this.libraryFile = libraryFile;
        this.globalId = globalId;
        this.skinType = skinType;
    }
    
    public SkinIdentifier(Skin skin) {
        this(skin.lightHash(), null, 0, skin.getSkinType());
    }
    
    public SkinIdentifier(ISkinIdentifier identifier) {
        this(identifier.getSkinLocalId(), identifier.getSkinLibraryFile(), identifier.getSkinGlobalId(), identifier.getSkinType());
    }
    
    @Override
    public boolean hasLocalId() {
        return localId != 0;
    }
    
    @Override
    public boolean hasLibraryFile() {
        return libraryFile != null;
    }
    
    @Override
    public boolean hasGlobalId() {
        return globalId != 0;
    }
    
    @Override
    public boolean isValid() {
        return hasLocalId() | hasLibraryFile() | hasGlobalId();
    }
    
    @Override
    public int getSkinLocalId() {
        return localId;
    }

    @Override
    public ILibraryFile getSkinLibraryFile() {
        return libraryFile;
    }

    @Override
    public int getSkinGlobalId() {
        return globalId;
    }
    
    @Override
    public ISkinType getSkinType() {
        return skinType;
    }

    @Override
    public String toString() {
        return "SkinIdentifier [localId=" + localId + ", libraryFile=" + libraryFile + ", globalId=" + globalId + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + globalId;
        result = prime * result + ((libraryFile == null) ? 0 : libraryFile.hashCode());
        result = prime * result + localId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkinIdentifier other = (SkinIdentifier) obj;
        if (globalId != other.globalId)
            return false;
        if (libraryFile == null) {
            if (other.libraryFile != null)
                return false;
        } else if (!libraryFile.equals(other.libraryFile))
            return false;
        if (localId != other.localId)
            return false;
        return true;
    }
    
    public enum SkinIdentifierType {
        LOCAL_DATABASE,
        LOCAL_FILE,
        GLOBAL_DATABASE
    }
}
