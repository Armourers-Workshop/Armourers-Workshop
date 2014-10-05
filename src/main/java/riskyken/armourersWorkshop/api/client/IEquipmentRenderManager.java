package riskyken.armourersWorkshop.api.client;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourPart;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;

public interface IEquipmentRenderManager {
    
    public void onLoad(IEquipmentRenderHandler handler);
    
    public void onRenderEquipment(Entity entity, EnumArmourType armourType);
    
    public void onRenderEquipmentPart(Entity entity, EnumArmourPart armourPart);
}
