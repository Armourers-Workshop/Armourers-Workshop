package riskyken.armourersWorkshop.api.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.client.event.EntityRenderEvent;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;

public class EquipmentRenderer {
    
    /**
     * Renders custom equipment at an entity.
     * @param entity Entity to render the armour on.
     * @param armourType Armour type to render.
     */
    public static void renderCustomEquipmentOnEntity(Entity entity, EnumArmourType armourType) {
        EntityRenderEvent.call(entity, armourType);
    }
    
    /**
     * Renders custom equipment at an entity.
     * @param entity Entity to render the armour on.
     * @param armourType Armour type to render.
     * @param modelBiped Optional ModelBiped that the equipment will use for rotation angles.
     */
    public static void renderCustomEquipmentOnEntity(Entity entity, EnumArmourType armourType, ModelBiped modelBiped) {
        EntityRenderEvent.call(entity, armourType, modelBiped);
    }
}
