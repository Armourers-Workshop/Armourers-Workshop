package riskyken.armourersWorkshop.api.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumArmourType;

public interface IEquipmentRenderHandler {

    /**
     * Renders custom equipment at an entity.
     * @param entity Entity to render the armour on.
     * @param armourType Armour type to render.
     * @param modelBiped Optional ModelBiped that the equipment will use for rotation angles.
     */
    public void renderCustomEquipmentOnEntity(Entity entity, EnumArmourType armourType, ModelBiped modelBiped);
    
    public int getItemModelRenderCacheSize();
    
    public int getPlayerModelRenderCacheSize();
}
