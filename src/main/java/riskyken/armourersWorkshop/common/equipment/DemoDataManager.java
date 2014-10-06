package riskyken.armourersWorkshop.common.equipment;

import riskyken.armourersWorkshop.api.common.equipment.IEquipmentCacheHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataManager;
import riskyken.armourersWorkshop.utils.ModLogger;

public class DemoDataManager implements IEquipmentDataManager {

    IEquipmentDataHandler dataHandler;
    IEquipmentCacheHandler cacheHandler;
    
    @Override
    public void onLoad(IEquipmentDataHandler dataHandler, IEquipmentCacheHandler cacheHandler) {
        this.dataHandler = dataHandler;
        this.cacheHandler = cacheHandler;
        ModLogger.log("Loaded DemoDataManager");
    }
}
