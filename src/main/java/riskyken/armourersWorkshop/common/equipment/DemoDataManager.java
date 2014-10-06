package riskyken.armourersWorkshop.common.equipment;

import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataManager;
import riskyken.armourersWorkshop.utils.ModLogger;

public class DemoDataManager implements IEquipmentDataManager {

    IEquipmentDataHandler dataHandler;
    
    @Override
    public void onLoad(IEquipmentDataHandler dataHandler) {
        this.dataHandler = dataHandler;
        ModLogger.log("Loaded DemoDataManager");
    }
}
