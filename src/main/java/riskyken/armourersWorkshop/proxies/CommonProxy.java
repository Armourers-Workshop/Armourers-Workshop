package riskyken.armourersWorkshop.proxies;

import java.util.UUID;

import net.minecraft.block.Block;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.network.messages.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import riskyken.armourersWorkshop.common.skin.data.Skin;

public class CommonProxy {
    
    public void preInit() {
        
    }
    
    public void initRenderers() {
        
    }
    
    public void init() {
        
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

    public void setPlayersNakedData(UUID playerId, EntityNakedInfo nakedInfo) {
        
    }
    
    public PlayerSkinInfo getPlayersNakedData(UUID playerId) {
        return null;
    }
    
    public int getRenderType(Block block) {
        return 0;
    }
    
    public void receivedEquipmentData(Skin equipmentData) {
        
    }
    
    public void receivedCommandFromSever(CommandType command) {
        
    }
    
    public void receivedEquipmentData(EntityEquipmentData equipmentData, int entityId) {
        
    }
}
