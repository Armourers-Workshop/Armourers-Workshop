package riskyken.armourersWorkshop.client.model;

import java.util.HashMap;

import riskyken.armourersWorkshop.common.skin.data.SkinDye;

public class ClientModelCache {
    
    public static ClientModelCache INSTANCE;
    
    private final HashMap<MultiKey, SkinModel> modelMap;
    private final SkinDye blankDye;
    
    public static void init() {
        INSTANCE = new ClientModelCache();
    }
    
    public ClientModelCache() {
        modelMap = new HashMap<MultiKey, SkinModel>();
        blankDye = new SkinDye();
    }
    /*
    public SkinModel getSkinModel(SkinPart skinPart, ISkinDye skinDye) {
        
    }
    */
    public class MultiKey {
        
        private final Object key1;
        private final Object key2;
        
        public MultiKey(Object key1, Object key2) {
            this.key1 = key1;
            this.key2 = key2;
        }

        @Override
        public int hashCode() {
            return key1.hashCode() ^ key2.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MultiKey other = (MultiKey) obj;
            if (key1 == null) {
                if (other.key1 != null)
                    return false;
            } else if (!key1.equals(other.key1))
                return false;
            if (key2 == null) {
                if (other.key2 != null)
                    return false;
            } else if (!key2.equals(other.key2))
                return false;
            return true;
        }
    }
}
