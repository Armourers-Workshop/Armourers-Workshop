package riskyken.armourersWorkshop.api.client;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.client.event.EntityRenderEvent;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;

public class EquipmentRenderer {
    
    public static void renderCustomEquipmentOnEntity(Entity entity, EnumArmourType armourType) {
        EntityRenderEvent.call(entity, armourType);
    }
}
