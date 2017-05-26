package riskyken.armourersWorkshop.proxies;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.library.CommonLibraryManager;
import riskyken.armourersWorkshop.common.library.ILibraryManager;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiAdminPanel.AdminPanelCommand;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public class CommonProxy {
    
    public ILibraryManager libraryManager;
    
    public void preInit() {
        
    }
    
    public void initLibraryManager() {
        libraryManager = new CommonLibraryManager();
    }
    
    public void initRenderers() {
        
    }
    
    public void init() {
        
    }
    
    public void postInit() {
        
    }
    
    public void registerKeyBindings() {
        
    }
    
    public void addEquipmentData(PlayerPointer playerPointer, EntityEquipmentData equipmentData) {
        
    }
    
    public int getPlayerModelCacheSize() {
        return 0;
    }
    
    public void receivedEquipmentData(Skin equipmentData) {
        
    }
    
    public void receivedCommandFromSever(CommandType command) {
        
    }
    
    public void receivedAdminPanelCommand(EntityPlayer player, AdminPanelCommand command) {
        switch (command) {
        case RECOVER_SKINS:
            SkinIOUtils.recoverSkins(player);
            break;
        case RELOAD_LIBRARY:
            ArmourersWorkshop.proxy.libraryManager.reloadLibrary();
            break;
        }
    }
    
    public void receivedEquipmentData(EntityEquipmentData equipmentData, int entityId) {
        
    }
    
    public void receivedSkinFromLibrary(String fileName, String filePath, Skin skin) {
        
    }
    
    public int getBlockRenderType(Block block) {
        return 0;
    }
    
    public MinecraftServer getServer() {
        return MinecraftServer.getServer();
    }
}
