package riskyken.armourersWorkshop.api.client.event;

import java.util.ArrayList;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.customEquipment.armour.EnumArmourType;

public class EntityRenderEvent {

    
    private static ArrayList<IEntityRenderListener> listeners = null;
    
    public static void addListener(IEntityRenderListener entityRenderEventListener) {
        if (listeners == null) {
            listeners = new ArrayList<IEntityRenderListener>();
        }
        listeners.add(entityRenderEventListener);
    }
    
    private static void callEvent(Entity entity, EnumArmourType armourType, ModelBiped modelBiped) {
        if (listeners != null) {
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).onEntityRenderEvent(entity, armourType, modelBiped);
            }
        }
    }
    
    public static void call(Entity entity, EnumArmourType armourType, ModelBiped modelBiped) {
        callEvent(entity, armourType, modelBiped);
    }
    
    public static void call(Entity entity, EnumArmourType armourType) {
        call(entity, armourType, null);
    }
    
    public static interface IEntityRenderListener {
        public abstract void onEntityRenderEvent(Entity entity, EnumArmourType armourType, ModelBiped modelBiped);
    }
}
