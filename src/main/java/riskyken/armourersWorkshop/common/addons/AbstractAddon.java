package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.common.addons.Addons.RenderType;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentDataManager;
import riskyken.armourersWorkshop.utils.EventState;


public abstract class AbstractAddon {
    
    public abstract void init();
    
    public abstract String getModName();
    
    public abstract void onWeaponRender(ItemRenderType type, EventState state);
    
    protected void addRenderClass(String className, RenderType renderType) {
        switch (renderType) {
        case SWORD:
            EntityEquipmentDataManager.INSTANCE.addSwordRenderClass(className);
            break;
        case BOW:
            EntityEquipmentDataManager.INSTANCE.addBowRenderClass(className);
            break;
        }
    }
}
