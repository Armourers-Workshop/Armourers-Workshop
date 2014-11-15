package riskyken.armourersWorkshop;

import net.minecraft.creativetab.CreativeTabs;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.ModFMLEventHandler;
import riskyken.armourersWorkshop.common.UpdateCheck;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.crafting.CraftingManager;
import riskyken.armourersWorkshop.common.creativetab.CreativeTabArmourersWorkshop;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentDataManager;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.GuiHandler;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.proxies.CommonProxy;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LibModInfo.ID, name = LibModInfo.NAME, version = LibModInfo.VERSION, guiFactory = LibModInfo.GUI_FACTORY_CLASS)
public class ArmourersWorkshop {

    @Mod.Instance(LibModInfo.ID)
    public static ArmourersWorkshop instance;

    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
    public static CommonProxy proxy;

    public static CreativeTabArmourersWorkshop tabArmorersWorkshop = new CreativeTabArmourersWorkshop(CreativeTabs.getNextID(), LibModInfo.ID.toLowerCase());

    @Mod.EventHandler
    public void perInit(FMLPreInitializationEvent event) {
        ModLogger.log("Loading " + LibModInfo.NAME + " " + LibModInfo.VERSION);
        ConfigHandler.init(event.getSuggestedConfigurationFile());

        UpdateCheck.checkForUpdates();
        
        ModItems.init();
        ModBlocks.init();
        
        proxy.init();
        proxy.initRenderers();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {
        CraftingManager.init();

        ModBlocks.registerTileEntities();

        new GuiHandler();
        
        //FMLInterModComms.sendMessage("armourersWorkshop", "register", "riskyken.armourersWorkshop.common.equipment.DemoDataManager");
        //FMLInterModComms.sendMessage("armourersWorkshop", "register", "riskyken.armourersWorkshop.client.render.DemoRenderManager");
        
        PacketHandler.init();
        proxy.postInit();
        proxy.registerKeyBindings();
        
        EntityEquipmentDataManager.init();
        FMLCommonHandler.instance().bus().register(new ModFMLEventHandler());
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        TileEntityArmourLibrary.createArmourDirectory();
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
