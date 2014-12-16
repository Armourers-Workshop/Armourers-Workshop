package riskyken.armourersWorkshop.common.equipment;

import net.minecraft.client.Minecraft;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataManager;

public class DemoDataManager implements IEquipmentDataManager {
    
    private Minecraft mc;
    private IEquipmentDataHandler dataHandler;
    
    public DemoDataManager() {
        this.mc = Minecraft.getMinecraft();
    }
    
    @Override
    public void onLoad(IEquipmentDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
}
