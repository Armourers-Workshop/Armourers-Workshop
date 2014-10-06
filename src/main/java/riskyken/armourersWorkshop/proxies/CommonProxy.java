package riskyken.armourersWorkshop.proxies;

import java.util.BitSet;
import java.util.UUID;

import net.minecraft.block.Block;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourItemData;

public class CommonProxy {
    
    public void init() {
        
    }
    
    public void initRenderers() {
        
    }
    
    public void postInit() {
        
    }
    
    public void registerKeyBindings() {
        
    }
    
    public void addEquipmentData(UUID playerId, EntityEquipmentData equipmentData) {
        
    }
    
    public void removeEquipmentData(UUID playerId) {
        
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
    
    public void receivedEquipmentData(CustomArmourItemData equipmentData, byte target) {
        
    }
}
