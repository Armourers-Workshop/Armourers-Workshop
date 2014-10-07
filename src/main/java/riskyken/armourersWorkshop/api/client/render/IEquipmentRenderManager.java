package riskyken.armourersWorkshop.api.client.render;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumArmourPart;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumArmourType;

/**
 * Used to handle rendering in the armourers workshop API.
 * To use create a class that implements IEquipmentRenderManager
 * then add this line to your mod's FMLInitializationEvent event.</BR>
 * </BR>
 * {@code FMLInterModComms.sendMessage("armourersWorkshop", "register", "full path to your class");}
 * 
 * @author RiskyKen
 *
 */
public interface IEquipmentRenderManager {
    
    public void onLoad(IEquipmentRenderHandler handler);
    
    public void onRenderEquipment(Entity entity, EnumArmourType armourType);
    
    public void onRenderEquipmentPart(Entity entity, EnumArmourPart armourPart);
}
