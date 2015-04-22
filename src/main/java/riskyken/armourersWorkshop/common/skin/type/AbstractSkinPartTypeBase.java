package riskyken.armourersWorkshop.common.skin.type;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public abstract class AbstractSkinPartTypeBase implements ISkinPartType {

    private ISkinType baseType;
    
    public AbstractSkinPartTypeBase(ISkinType baseType) {
        this.baseType = baseType;
    }
    
    @Override
    public String getRegistryName() {
        return baseType.getRegistryName() + "." + getPartName();
    }
}
