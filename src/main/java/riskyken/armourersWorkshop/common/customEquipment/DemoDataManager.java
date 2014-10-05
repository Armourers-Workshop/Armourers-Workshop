package riskyken.armourersWorkshop.common.customEquipment;

import riskyken.armourersWorkshop.api.common.customEquipment.IEquipmentCacheHandler;
import riskyken.armourersWorkshop.api.common.customEquipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.customEquipment.IEquipmentDataManager;
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
