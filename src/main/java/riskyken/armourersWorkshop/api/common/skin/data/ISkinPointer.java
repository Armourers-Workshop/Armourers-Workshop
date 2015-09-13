package riskyken.armourersWorkshop.api.common.skin.data;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public interface ISkinPointer {

    public int getSkinId();
    
    public ISkinType getSkinType();
    
    public ISkinDye getSkinDye();
}
