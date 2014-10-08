package riskyken.armourersWorkshop.client.handler;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentType;
import riskyken.armourersWorkshop.client.render.EquipmentItemRenderCache;
import riskyken.armourersWorkshop.client.render.EquipmentPlayerRenderCache;

public class EquipmentRenderHandler implements IEquipmentRenderHandler {

    public static final EquipmentRenderHandler INSTANCE = new EquipmentRenderHandler();
    
    @Override
    public void renderCustomEquipmentOnEntity(Entity entity, EnumEquipmentType armourType, ModelBiped modelBiped) {
        EquipmentPlayerRenderCache.INSTANCE.renderEquipmentPartOnEntity(entity, armourType, modelBiped);
    }

    @Override
    public void renderCustomEquipment(int equipmentId, ModelBiped modelBiped) {
        EquipmentPlayerRenderCache.INSTANCE.renderEquipmentPart(equipmentId, modelBiped);
    }
    
    @Override
    public int getItemModelRenderCacheSize() {
        return EquipmentItemRenderCache.getCacheSize();
    }

    @Override
    public int getEntityModelRenderCacheSize() {
        return EquipmentPlayerRenderCache.INSTANCE.getCacheSize();
    }
}
