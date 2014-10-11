package riskyken.armourersWorkshop.api.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;

public interface IEquipmentRenderHandler {

    /**
     * Renders custom equipment at an entity using it's IEntityEquipment data.
     * @param entity Entity to render the armour on.
     * @param armourType Armour type to render.
     * @param modelBiped Optional ModelBiped that the equipment will use for rotation angles.
     * Pass null if you do not want to use this.
     */
    public void renderCustomEquipmentOnEntity(Entity entity, EnumEquipmentType armourType, ModelBiped modelBiped);
    
    /**
     * Renders custom equipment.
     * @param equipmentId Equipment id to render.
     * @param modelBiped Optional ModelBiped that the equipment will use for rotation angles.
     * Pass null if you do not want to use this.
     */
    public void renderCustomEquipment(int equipmentId, ModelBiped modelBiped);
    
    /**
     * Get the number of items in the cache for item models.
     * @return Number of items in the cache.
     */
    public int getItemModelRenderCacheSize();
    
    /**
     * Get the number of items in the cache for entity models.
     * @return Number of items in the cache.
     */
    public int getEntityModelRenderCacheSize();
}
