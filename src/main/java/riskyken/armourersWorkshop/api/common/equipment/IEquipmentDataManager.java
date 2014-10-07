package riskyken.armourersWorkshop.api.common.equipment;

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
public interface IEquipmentDataManager {
    
    public void onLoad(IEquipmentDataHandler dataHandler);
}
