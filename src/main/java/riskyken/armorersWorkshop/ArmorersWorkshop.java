package riskyken.armorersWorkshop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armorersWorkshop.common.ModFMLEventHandler;
import riskyken.armorersWorkshop.common.ModForgeEventHandler;
import riskyken.armorersWorkshop.common.blocks.ModBlocks;
import riskyken.armorersWorkshop.common.config.ConfigHandler;
import riskyken.armorersWorkshop.common.crafting.CraftingManager;
import riskyken.armorersWorkshop.common.creativetab.CreativeTabArmorersWorkshop;
import riskyken.armorersWorkshop.common.items.ModItems;
import riskyken.armorersWorkshop.common.lib.LibModInfo;
import riskyken.armorersWorkshop.proxies.CommonProxy;
import riskyken.armorersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LibModInfo.ID, name = LibModInfo.NAME, version = LibModInfo.VERSION, guiFactory = LibModInfo.GUI_FACTORY_CLASS)
public class ArmorersWorkshop {

	@Mod.Instance(LibModInfo.ID)
	public static ArmorersWorkshop instance;
	
	@SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
	public static CommonProxy proxy;
	
	public static CreativeTabArmorersWorkshop tabArmorersWorkshop = new CreativeTabArmorersWorkshop(CreativeTabs.getNextID(),LibModInfo.ID);
	
	@Mod.EventHandler
	public void perInit(FMLPreInitializationEvent event) {
		ModLogger.log("Loading " + LibModInfo.NAME + " " + LibModInfo.VERSION);
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		
		ModItems.init();
		ModBlocks.init();
		
		proxy.init();
		proxy.initRenderers();
	}

	@Mod.EventHandler
	public void load(FMLInitializationEvent event){
		CraftingManager.init();
		
		ModBlocks.registerTileEntities();
		
		//new GuiHandler();
		
	    //PacketHandler.init();
	    proxy.postInit();
	    
	    MinecraftForge.EVENT_BUS.register(new ModForgeEventHandler());
	    FMLCommonHandler.instance().bus().register(new ModFMLEventHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
