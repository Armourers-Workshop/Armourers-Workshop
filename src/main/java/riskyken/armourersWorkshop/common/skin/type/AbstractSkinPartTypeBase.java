package riskyken.armourersWorkshop.common.skin.type;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
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
    
    @Override
    public int getMinimumMarkersNeeded() {
        return 0;
    }
    
    @Override
    public int getMaximumMarkersNeeded() {
        return 0;
    }
    
    @Override
    public boolean isPartRequired() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IPoint3D getItemRenderOffset() {
        return new Point3D(0, 0, 0);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IRectangle3D getItemRenderTextureBounds() {
        return null;
    }
}
