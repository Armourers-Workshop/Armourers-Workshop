package riskyken.armourersWorkshop;

import net.minecraft.creativetab.CreativeTabs;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.addons.Addons;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.command.CommandArmourers;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.crafting.CraftingManager;
import riskyken.armourersWorkshop.common.creativetab.CreativeTabArmourersWorkshop;
import riskyken.armourersWorkshop.common.download.SkinDownloadManager;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.GuiHandler;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentDataManager;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.cubes.CubeFactory;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.update.UpdateCheck;
import riskyken.armourersWorkshop.proxies.CommonProxy;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import riskyken.minecraftWrapper.common.creativetab.ModCreativeTab;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = LibModInfo.ID, name = LibModInfo.NAME, version = LibModInfo.VERSION, guiFactory = LibModInfo.GUI_FACTORY_CLASS)
public class ArmourersWorkshop {

    @Mod.Instance(LibModInfo.ID)
    public static ArmourersWorkshop instance;

    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
    public static CommonProxy proxy;

    public static ModCreativeTab creativeTabArmorersWorkshop = new ModCreativeTab(LibModInfo.ID);
    public static CreativeTabArmourersWorkshop tabArmorersWorkshop = new CreativeTabArmourersWorkshop(CreativeTabs.getNextID(), LibModInfo.ID.toLowerCase());

    public static ModItems modItems;
    public static ModBlocks modBlocks;
    
    @Mod.EventHandler
    public void perInit(FMLPreInitializationEvent event) {
        ModLogger.log("Loading " + LibModInfo.NAME + " " + LibModInfo.VERSION);
        creativeTabArmorersWorkshop.setMinecraftCreativeTab(tabArmorersWorkshop);
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        Addons.preInit();
        proxy.preInit();
        
        SkinIOUtils.makeLibraryDirectory();
        UpdateCheck.checkForUpdates();
        SkinDownloadManager.downloadSkins();
        
        modItems = new ModItems();
        modBlocks = new ModBlocks();
        
        SkinTypeRegistry.init();
        CubeFactory.init();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        CraftingManager.init();

        modBlocks.registerTileEntities();

        new GuiHandler();
        
        PacketHandler.init();
        EntityEquipmentDataManager.init();
        EntitySkinHandler.init();
        
        proxy.init();
        proxy.registerKeyBindings();
        proxy.initRenderers();
        
        Addons.init();
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
        Addons.postInit();
    }
    
    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandArmourers());
    }
    
    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        if (SkinDataCache.INSTANCE != null) {
            SkinDataCache.INSTANCE.clearAll();
        }
    }
    
    @Mod.EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        for (IMCMessage imcMessage : event.getMessages()) {
            if (!imcMessage.isStringMessage()) continue;
            if (imcMessage.key.equalsIgnoreCase("register")) {
                ModLogger.log(String.format("Receiving registration request from %s for class %s", imcMessage.getSender(), imcMessage.getStringValue()));
                ApiRegistrar.INSTANCE.addApiRequest(imcMessage.getSender(), imcMessage.getStringValue());
            }
        }
    }
}
