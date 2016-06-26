package riskyken.armourersWorkshop.api.common;

import riskyken.armourersWorkshop.api.common.skin.ISkinDataHandler;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinTypeRegistry;

/**
 * Used to handle equipment data in the armourers workshop API.
 * To use create a class that implements IEquipmentDataManager
 * then add this line to your mod's FMLInitializationEvent event.</BR>
 * </BR>
 * {@code FMLInterModComms.sendMessage("armourersWorkshop", "register", "full path to your class");}
 * 
 * @author RiskyKen
 *
 */
public interface IArmourersCommonManager {
    
    public void onLoad(ISkinDataHandler skinDataHandler, ISkinTypeRegistry skinTypeRegistry);
}
