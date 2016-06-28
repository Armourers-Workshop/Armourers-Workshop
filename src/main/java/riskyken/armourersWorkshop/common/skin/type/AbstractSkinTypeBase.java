package riskyken.armourersWorkshop.common.skin.type;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public abstract class AbstractSkinTypeBase implements ISkinType {
    
    @Override
    public boolean showSkinOverlayCheckbox() {
        return false;
    }
    
    @Override
    public boolean showHelperCheckbox() {
        return false;
    }
    
    @Override
    public int getVanillaArmourSlotId() {
        return -1;
    }
    
    @Override
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public boolean enabled() {
        return true;
    }
}
