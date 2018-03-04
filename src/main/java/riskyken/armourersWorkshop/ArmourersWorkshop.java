package riskyken.armourersWorkshop;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import net.minecraft.creativetab.CreativeTabs;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.command.CommandArmourers;
import riskyken.armourersWorkshop.common.creativetab.CreativeTabArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.proxies.CommonProxy;
import riskyken.armourersWorkshop.utils.ModLogger;

@Mod(modid = LibModInfo.ID, name = LibModInfo.NAME, version = LibModInfo.VERSION, guiFactory = LibModInfo.GUI_FACTORY_CLASS)
public class ArmourersWorkshop {
    
    /* 
     * Hello and welcome to the Armourer's Workshop source code.
     * 
     * Please read this to familiarise yourself with the different terms used in the code.
     * 
     * Important - Any time the texture that is used on the player model is referred to,
     * (normal called the players skin) it will be called the player texture or entity
     * texture to prevent confusion with AW skins.
     * 
     * Skin - A custom 3D model that can be created by a player. Skins are stored in CommonSkinCache
     * server side and ClientSkinCache on the client side.
     * 
     * SkinType - Each skin has a skin type, examples; head, chest, bow and block. All
     * skin types can be found in the SkinTypeRegistry.
     * 
     * SkinPart - Each skin type will have at least 1 skin part. For example the chest skin type has
     * base, left arm and right arm skin parts.
     * 
     * SkinPartType - Each skin part has a part type examples; left arm, left leg and right arm.
     */
    
    @Instance(LibModInfo.ID)
    public static ArmourersWorkshop instance;

    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
    public static CommonProxy proxy;

    public static CreativeTabArmourersWorkshop tabArmorersWorkshop = new CreativeTabArmourersWorkshop(CreativeTabs.getNextID(), LibModInfo.ID.toLowerCase());


    
    @EventHandler
    public void perInit(FMLPreInitializationEvent event) {
        ModLogger.log(String.format("Loading %s version %s", LibModInfo.NAME, LibModInfo.VERSION));
        proxy.preInit(event);
        proxy.initLibraryManager();
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.init(event);
        proxy.registerKeyBindings();
        proxy.initRenderers();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
    
    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandArmourers());
        CommonSkinCache.INSTANCE.serverStarted();
    }
    
    @EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        CommonSkinCache.INSTANCE.serverStopped();
    }
    
    @EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        for (IMCMessage imcMessage : event.getMessages()) {
            if (!imcMessage.isStringMessage()) continue;
            if (imcMessage.key.equalsIgnoreCase("register")) {
                ModLogger.log(String.format("Receiving registration request from %s for class %s", imcMessage.getSender(), imcMessage.getStringValue()));
                ApiRegistrar.INSTANCE.addApiRequest(imcMessage.getSender(), imcMessage.getStringValue());
            }
        }
    }
    
    public static boolean isDedicated() {
        return proxy.getClass() == CommonProxy.class;
    }
    
    public static CommonProxy getProxy() {
        return proxy;
    }
    
    public static ArmourersWorkshop getInstance() {
        return instance;
    }
}
