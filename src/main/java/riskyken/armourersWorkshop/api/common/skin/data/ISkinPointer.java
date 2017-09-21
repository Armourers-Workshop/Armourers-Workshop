package riskyken.armourersWorkshop.api.common.skin.data;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;

public interface ISkinPointer {

    public SkinIdentifier getIdentifier();
    
    /**
     * @deprecated  As of 0.48.0, replaced by {@link #getIdentifier()}
     */
    @Deprecated
    public int getSkinId();
    
    public ISkinType getSkinType();
    
    public ISkinDye getSkinDye();
}
