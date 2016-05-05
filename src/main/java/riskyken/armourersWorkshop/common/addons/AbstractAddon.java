package riskyken.armourersWorkshop.common.addons;

import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;


public abstract class AbstractAddon {
    
    public abstract void preInit();
    
    public abstract void init();
    
    public abstract void postInit();
    
    public abstract String getModId();
    
    public abstract String getModName();
    
    public abstract void onWeaponRender(ItemRenderType type, EventState state);
}
