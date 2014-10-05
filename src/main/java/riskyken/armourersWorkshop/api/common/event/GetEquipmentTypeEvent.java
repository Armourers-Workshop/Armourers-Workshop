package riskyken.armourersWorkshop.api.common.event;

import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;

public class GetEquipmentTypeEvent {
    
    private static ArrayList<IGetEquipmentTypeListener> listeners = null;
    
    public static void addListener(IGetEquipmentTypeListener entityRenderEventListener) {
        if (listeners == null) {
            listeners = new ArrayList<IGetEquipmentTypeListener>();
        }
        listeners.add(entityRenderEventListener);
    }
    
    private static EnumArmourType callEvent(int equipmentId) {
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                EnumArmourType returnType = listeners.get(i).onGetEquipmentTypeEvent(equipmentId);
                if (returnType != EnumArmourType.NONE) {
                    return returnType;
                }
            }
        }
        return EnumArmourType.NONE;
    }
    
    public static EnumArmourType call(int equipmentId) {
        return callEvent(equipmentId);
    }
    
    public static interface IGetEquipmentTypeListener {
        public abstract EnumArmourType onGetEquipmentTypeEvent(int equipmentId);
    }
}
