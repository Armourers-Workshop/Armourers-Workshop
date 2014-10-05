package riskyken.armourersWorkshop.api.common.event;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;

public class AddEntityEquipmentEvent {
    
    private static ArrayList<IAddEntityEquipmentListener> listeners = null;
    
    public static void addListener(IAddEntityEquipmentListener entityRenderEventListener) {
        if (listeners == null) {
            listeners = new ArrayList<IAddEntityEquipmentListener>();
        }
        listeners.add(entityRenderEventListener);
    }
    
    private static void callEvent(Entity entity, EnumArmourType armourType, int equipmentId) {
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onAddEntityEquipmentEvent(entity, armourType, equipmentId);
            }
        }
    }
    
    public static void call(Entity entity, EnumArmourType armourType, int equipmentId) {
        callEvent(entity, armourType, equipmentId);
    }
    
    public static interface IAddEntityEquipmentListener {
        public abstract void onAddEntityEquipmentEvent(Entity entity, EnumArmourType armourType, int equipmentId);
    }
}
