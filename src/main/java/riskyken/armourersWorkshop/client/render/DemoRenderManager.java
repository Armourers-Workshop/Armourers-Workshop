package riskyken.armourersWorkshop.client.render;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.client.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.client.IEquipmentRenderManager;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourPart;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.utils.ModLogger;

public class DemoRenderManager implements IEquipmentRenderManager {

    @Override
    public void onLoad(IEquipmentRenderHandler handler) {
        ModLogger.log("Loaded DemoRenderManager");
    }

    @Override
    public void onRenderEquipment(Entity entity, EnumArmourType armourType) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onRenderEquipmentPart(Entity entity, EnumArmourPart armourPart) {
        // TODO Auto-generated method stub
        
    }
}
