package riskyken.armourersWorkshop.proxies;

import riskyken.armourersWorkshop.common.customarmor.ArmourPart;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.CustomArmourData;

public interface IProxy {

    public abstract void init();
    
    public abstract void initRenderers();
    
    public abstract void postInit();
    
    public abstract void addCustomArmour(String playerName, CustomArmourData armourData);
    
    public abstract void removeCustomArmour(String playerName, ArmourerType type, ArmourPart part);
    
    public abstract void removeAllCustomArmourData(String playerName);
    
    public abstract boolean playerHasSkirt(String playerName);
}
