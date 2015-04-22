package riskyken.armourersWorkshop.api.client;

import riskyken.armourersWorkshop.api.client.render.ISkinRenderHandler;


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
public interface IArmourersClientManager {
    
    public void onLoad(ISkinRenderHandler handler);
}
