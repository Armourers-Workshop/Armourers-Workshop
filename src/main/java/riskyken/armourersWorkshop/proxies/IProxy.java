package riskyken.armourersWorkshop.proxies;

import riskyken.armourersWorkshop.common.customarmor.ArmourType;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;

public interface IProxy {

    public abstract void init();
    
    public abstract void initRenderers();
    
    public abstract void postInit();
    
    public abstract void addCustomArmour(String playerName, CustomArmourItemData armourData);
    
    public abstract void removeCustomArmour(String playerName, ArmourType type);
    
    public abstract void removeAllCustomArmourData(String playerName);
    
    public abstract boolean playerHasSkirt(String playerName);
}
