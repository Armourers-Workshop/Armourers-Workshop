package riskyken.armourersWorkshop.proxies;

import java.util.BitSet;

import net.minecraft.block.Block;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;

public class CommonProxy {
    
    public void init() {
        
    }
    
    public void initRenderers() {
        
    }
    
    public void postInit() {
        
    }
    
    public void registerKeyBindings() {
        
    }
    
    public void addCustomArmour(String playerName, CustomArmourItemData armourData) {
        
    }
    
    public void removeCustomArmour(String playerName, ArmourType type) {
        
    }
    
    public void removeAllCustomArmourData(String playerName) {
        
    }
    
    public int getPlayerModelCacheSize() {
        return 0;
    }

    public void setPlayersNakedData(String playerName, boolean isNaked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        
    }
    
    public PlayerSkinInfo getPlayersNakedData(String playerName) {
        return null;
    }
    
    public int getRenderType(Block block) {
        return 0;
    }
    
    public void receivedEquipmentData(CustomArmourItemData equipmentData) {
        
    }
}
