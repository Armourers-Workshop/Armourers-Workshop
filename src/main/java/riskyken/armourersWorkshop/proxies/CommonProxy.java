package riskyken.armourersWorkshop.proxies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.library.CommonLibraryManager;
import riskyken.armourersWorkshop.common.library.ILibraryCallback;
import riskyken.armourersWorkshop.common.library.ILibraryManager;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.library.LibraryFileType;
import riskyken.armourersWorkshop.common.library.global.permission.PermissionSystem;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiAdminPanel.AdminPanelCommand;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSkinLibraryCommand.SkinLibraryCommand;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerLibrarySendSkin.SendType;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public class CommonProxy implements ILibraryCallback {
    
    public ILibraryManager libraryManager;
    private PermissionSystem permissionSystem;
    
    public void preInit() {
        
    }
    
    public void initLibraryManager() {
        libraryManager = new CommonLibraryManager();
    }
    
    public void initRenderers() {
        
    }
    
    public void init() {
        permissionSystem = new PermissionSystem();
    }
    
    public void postInit() {
        
    }
    
    public PermissionSystem getPermissionSystem() {
        return permissionSystem;
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
        case UPDATE_SKINS:
            SkinIOUtils.updateSkins(player);
            break;
        }
    }
    
    public void receivedEquipmentData(EntityEquipmentData equipmentData, int entityId) {
        
    }
    
    public void receivedSkinFromLibrary(String fileName, String filePath, Skin skin, SendType sendType) {
        
    }
    
    public int getBlockRenderType(Block block) {
        return 0;
    }
    
    public MinecraftServer getServer() {
        return MinecraftServer.getServer();
    }

    public void skinLibraryCommand(EntityPlayerMP player, SkinLibraryCommand command, LibraryFile file, boolean publicList) {
        if (!publicList) {
            //file = new LibraryFile(file.fileName, "/private/" + player.getUniqueID().toString() + file.filePath, file.skinType, file.isDirectory());
        }
        switch (command) {
        case DELETE:
            if (!publicList) {
                File dir = new File(SkinIOUtils.getSkinLibraryDirectory(), file.filePath);
                if (file.isDirectory()) {
                    dir = new File(dir, file.fileName + "/");
                } else {
                    dir = new File(dir, file.fileName + SkinIOUtils.SKIN_FILE_EXTENSION);
                }
                if (dir.isDirectory() == file.isDirectory()) {
                    if (dir.exists()) {
                        if (file.isDirectory()) {
                            try {
                                FileUtils.deleteDirectory(dir);
                                libraryManager.reloadLibrary();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            clearFiles.add(file);
                            ModLogger.log("deleting skin " + dir.getAbsolutePath());
                            dir.delete();
                            libraryManager.removeFileFromListType(file, LibraryFileType.SERVER_PRIVATE, player);
                            libraryManager.reloadLibrary(this);
                        }
                    }
                }
            } else {
                
                ModLogger.log("public delete");
            }
            break;
        case NEW_FOLDER:
            if (!publicList) {
                File dir = new File(SkinIOUtils.getSkinLibraryDirectory(), file.filePath);
                ModLogger.log(dir.getAbsolutePath());
                dir = new File(dir, file.fileName);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                //TODO don't reload the library just add the folder
                libraryManager.reloadLibrary();
                ModLogger.log(String.format("making folder call %s in %s", file.fileName, file.filePath));
                ModLogger.log("full path: " + dir.getAbsolutePath());
            } else {
                ModLogger.log("public new folder");
            }
            break;
        }
    }
    
    private ArrayList<LibraryFile> clearFiles = new ArrayList<LibraryFile>();

    @Override
    public void libraryReloaded(ILibraryManager libraryManager) {
        for (int i = 0; i < clearFiles.size(); i++) {
            CommonSkinCache.INSTANCE.clearFileNameIdLink(clearFiles.get(i));
        }
    }
}
