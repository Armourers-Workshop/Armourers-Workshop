package riskyken.armourersWorkshop.common.skin.type;

import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public abstract class AbstractSkinPartTypeBase implements ISkinPartType {

    private ISkinType baseType;
    protected IRectangle3D buildingSpace;
    protected IRectangle3D guideSpace;
    protected IPoint3D offset;
    
    public AbstractSkinPartTypeBase(ISkinType baseType) {
        this.baseType = baseType;
    }
    
    @Override
    public IRectangle3D getBuildingSpace() {
        return this.buildingSpace;
    }

    @Override
    public IRectangle3D getGuideSpace() {
        return this.guideSpace;
    }

    @Override
    public IPoint3D getOffset() {
        return this.offset;
    }
    
    @Override
    public String getRegistryName() {
        return baseType.getRegistryName() + "." + getPartName();
    }
}
