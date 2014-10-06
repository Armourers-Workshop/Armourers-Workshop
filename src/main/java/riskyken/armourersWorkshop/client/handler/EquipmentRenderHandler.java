package riskyken.armourersWorkshop.client.handler;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumArmourType;
import riskyken.armourersWorkshop.client.render.EquipmentItemRenderCache;
import riskyken.armourersWorkshop.client.render.EquipmentPlayerRenderCache;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsEntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourItemData;

public class EquipmentRenderHandler implements IEquipmentRenderHandler {

    public static final EquipmentRenderHandler INSTANCE = new EquipmentRenderHandler();
    
    @Override
    public void renderCustomEquipmentOnEntity(Entity entity, EnumArmourType armourType, ModelBiped modelBiped) {
        ExtendedPropsEntityEquipmentData entityProps = ExtendedPropsEntityEquipmentData.get(entity);
        if (entityProps == null) {
            return;
        }
        
        EntityEquipmentData equipmentData = entityProps.getEquipmentData();
        
        if (equipmentData.haveEquipment(armourType)) {
            CustomArmourItemData data = EquipmentPlayerRenderCache.INSTANCE.getCustomArmourItemData(equipmentData.getEquipmentId(armourType));
            if (data != null) {
                switch (armourType) {
                case NONE:
                    break;
                case HEAD:
                    EquipmentPlayerRenderCache.INSTANCE.customHead.render(entity, modelBiped, data);
                    break;
                case CHEST:
                    EquipmentPlayerRenderCache.INSTANCE.customChest.render(entity, modelBiped, data);
                    break;
                case LEGS:
                    EquipmentPlayerRenderCache.INSTANCE.customLegs.render(entity, modelBiped, data);
                    break;
                case SKIRT:
                    EquipmentPlayerRenderCache.INSTANCE.customSkirt.render(entity, modelBiped, data);
                    break;
                case FEET:
                    EquipmentPlayerRenderCache.INSTANCE.customFeet.render(entity, modelBiped, data);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemModelRenderCacheSize() {
        return EquipmentItemRenderCache.getCacheSize();
    }

    @Override
    public int getPlayerModelRenderCacheSize() {
        return EquipmentPlayerRenderCache.INSTANCE.getCacheSize();
    }
}
