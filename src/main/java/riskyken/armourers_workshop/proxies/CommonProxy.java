package riskyken.armourers_workshop.proxies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.common.addons.ModAddonManager;
import riskyken.armourers_workshop.common.blocks.BlockSkinnable.Seat;
import riskyken.armourers_workshop.common.blocks.ModBlocks;
import riskyken.armourers_workshop.common.config.ConfigHandler;
import riskyken.armourers_workshop.common.config.ConfigHandlerClient;
import riskyken.armourers_workshop.common.config.ConfigHandlerOverrides;
import riskyken.armourers_workshop.common.config.ConfigSynchronizeHandler;
import riskyken.armourers_workshop.common.crafting.CraftingManager;
import riskyken.armourers_workshop.common.data.PlayerPointer;
import riskyken.armourers_workshop.common.items.ModItems;
import riskyken.armourers_workshop.common.lib.LibModInfo;
import riskyken.armourers_workshop.common.library.CommonLibraryManager;
import riskyken.armourers_workshop.common.library.ILibraryCallback;
import riskyken.armourers_workshop.common.library.ILibraryManager;
import riskyken.armourers_workshop.common.library.LibraryFile;
import riskyken.armourers_workshop.common.library.LibraryFileType;
import riskyken.armourers_workshop.common.library.global.permission.PermissionSystem;
import riskyken.armourers_workshop.common.network.GuiHandler;
import riskyken.armourers_workshop.common.network.PacketHandler;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiAdminPanel.AdminPanelCommand;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiSkinLibraryCommand.SkinLibraryCommand;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin.SendType;
import riskyken.armourers_workshop.common.skin.EntityEquipmentData;
import riskyken.armourers_workshop.common.skin.EntityEquipmentDataManager;
import riskyken.armourers_workshop.common.skin.SkinExtractor;
import riskyken.armourers_workshop.common.skin.cache.CommonSkinCache;
import riskyken.armourers_workshop.common.skin.cubes.CubeRegistry;
import riskyken.armourers_workshop.common.skin.data.Skin;
import riskyken.armourers_workshop.common.skin.entity.EntitySkinHandler;
import riskyken.armourers_workshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourers_workshop.common.update.UpdateCheck;
import riskyken.armourers_workshop.utils.ModLogger;
import riskyken.armourers_workshop.utils.SkinIOUtils;

public class CommonProxy implements ILibraryCallback {
    
    private static ModItems modItems;
    private static ModBlocks modBlocks;
    public ILibraryManager libraryManager;
    private PermissionSystem permissionSystem;
    
    public void preInit(FMLPreInitializationEvent event) {
        File configDir = event.getSuggestedConfigurationFile().getParentFile();
        configDir = new File(configDir, LibModInfo.ID);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        ModAddonManager.preInit();
        ConfigHandler.init(new File(configDir, "common.cfg"));
        ConfigHandlerClient.init(new File(configDir, "client.cfg"));
        ConfigHandlerOverrides.init(new File(configDir, "overrides.cfg"));
        
        EntityRegistry.registerModEntity(new ResourceLocation(LibModInfo.ID, "seat"), Seat.class, "seat", 1, ArmourersWorkshop.instance, 10, 20, false);
        
        SkinIOUtils.makeLibraryDirectory();
        UpdateCheck.checkForUpdates();
        SkinExtractor.extractSkins();
        
        SkinTypeRegistry.init();
        CubeRegistry.init();
        
        modItems = new ModItems();
        modBlocks = new ModBlocks();
    }
    
    public void initLibraryManager() {
        libraryManager = new CommonLibraryManager();
    }
    
    public void initRenderers() {}
    
    public void init(FMLInitializationEvent event) {
        modBlocks.registerTileEntities();
        CraftingManager.init();
        new GuiHandler();
        new ConfigSynchronizeHandler();
        
        PacketHandler.init();
        EntityEquipmentDataManager.init();
        EntitySkinHandler.init();
        permissionSystem = new PermissionSystem();
        ModAddonManager.init();
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        ModAddonManager.postInit();
        libraryManager.reloadLibrary();
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

    public void skinLibraryCommand(EntityPlayerMP player, SkinLibraryCommand command, LibraryFile file, boolean publicList) {
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
                    if (!SkinIOUtils.isInLibraryDir(dir)) {
                        ModLogger.log(Level.WARN, String.format("Player '%s' tried to delete the file/folder '%s' that is outside the library directory.", player.getGameProfile().toString(), dir.getAbsolutePath()));
                        return;
                    }
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
                dir = new File(dir, file.fileName);
                if (!SkinIOUtils.isInLibraryDir(dir)) {
                    ModLogger.log(Level.WARN, String.format("Player '%s' tried to make the folder '%s' that is outside the library directory.", player.getGameProfile().toString(), dir.getAbsolutePath()));
                    return;
                }
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
    
    public boolean isLocalPlayer(String username) {
        return false;
    }
    
    public boolean haveFullLocalProfile() {
        return false;
    }
    
    public GameProfile getLocalGameProfile() {
        return null;
    }
}
