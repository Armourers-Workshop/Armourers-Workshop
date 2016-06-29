package riskyken.armourersWorkshop.common.skin.type;

import net.minecraft.inventory.EntityEquipmentSlot;
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
    public EntityEquipmentSlot getEntityEquipmentSlot() {
        return null;
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
