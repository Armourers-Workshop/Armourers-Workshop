package riskyken.plushieWrapper;

import org.apache.logging.log4j.Logger;

import riskyken.plushieWrapper.common.lib.LibModInfo;
import riskyken.plushieWrapper.proxies.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = LibModInfo.MOD_ID, name = LibModInfo.MOD_NAME, version = LibModInfo.MOD_VERSION)
public class PlushieWrapper {
    
    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
    public static CommonProxy proxy;
    
    public Logger logger;
    
    @Mod.EventHandler
    public void perInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info(String.format("Loading %s version %s", LibModInfo.MOD_NAME, LibModInfo.MOD_VERSION));
        proxy.preInit();
    }
    
    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        proxy.init();
    }
    
    @Mod.EventHandler
    public void postInit(FMLPreInitializationEvent event) {
        proxy.postInit();
    }
}
