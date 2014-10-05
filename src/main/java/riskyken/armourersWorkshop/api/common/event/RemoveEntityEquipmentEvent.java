package riskyken.armourersWorkshop.api.common.event;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;

public class RemoveEntityEquipmentEvent {
    
    private static ArrayList<IRemoveEntityEquipmentListener> listeners = null;
    
    public static void addListener(IRemoveEntityEquipmentListener entityRenderEventListener) {
        if (listeners == null) {
            listeners = new ArrayList<IRemoveEntityEquipmentListener>();
        }
        listeners.add(entityRenderEventListener);
    }
    
    private static void callEvent(Entity entity, EnumArmourType armourType) {
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onRemoveEntityEquipmentEvent(entity, armourType);
            }
        }
    }
    
    public static void call(Entity entity, EnumArmourType armourType) {
        callEvent(entity, armourType);
    }
    
    public static interface IRemoveEntityEquipmentListener {
        public abstract void onRemoveEntityEquipmentEvent(Entity entity, EnumArmourType armourType);
    }
}
