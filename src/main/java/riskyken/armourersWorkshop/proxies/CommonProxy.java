package riskyken.armourersWorkshop.proxies;

import java.util.BitSet;
import java.util.UUID;

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
    
    public void addCustomArmour(UUID playerId, CustomArmourItemData armourData) {
        
    }
    
    public void removeCustomArmour(UUID playerId, ArmourType type) {
        
    }
    
    public void removeAllCustomArmourData(UUID playerId) {
        
    }
    
    public int getPlayerModelCacheSize() {
        return 0;
    }

    public void setPlayersNakedData(UUID playerId, boolean isNaked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        
    }
    
    public PlayerSkinInfo getPlayersNakedData(UUID playerId) {
        return null;
    }
    
    public int getRenderType(Block block) {
        return 0;
    }
    
    public void receivedEquipmentData(CustomArmourItemData equipmentData) {
        
    }
}
