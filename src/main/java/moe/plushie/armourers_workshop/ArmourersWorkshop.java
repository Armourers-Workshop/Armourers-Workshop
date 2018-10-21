package moe.plushie.armourers_workshop;

import java.util.Map;

import moe.plushie.armourers_workshop.common.ApiRegistrar;
import moe.plushie.armourers_workshop.common.command.CommandArmourers;
import moe.plushie.armourers_workshop.common.creativetab.CreativeTabArmourersWorkshop;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.proxies.CommonProxy;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = LibModInfo.ID, name = LibModInfo.NAME, version = LibModInfo.MOD_VERSION, guiFactory = LibModInfo.GUI_FACTORY_CLASS, dependencies = LibModInfo.DEPENDENCIES, acceptedMinecraftVersions = LibModInfo.MC_VERSION)
public class ArmourersWorkshop {

    /*
     * Hello and welcome to the Armourer's Workshop source code.
     * 
     * Note: Any time the texture that is used on the player model is referred to, (normal called the players skin) it will be called the
     * player texture or entity texture to prevent confusion with AW skins.
     */

    @Instance(LibModInfo.ID)
    public static ArmourersWorkshop instance;

    @SidedProxy(clientSide = LibModInfo.PROXY_CLIENT_CLASS, serverSide = LibModInfo.PROXY_COMMNON_CLASS)
    public static CommonProxy proxy;

    public static CreativeTabArmourersWorkshop tabArmorersWorkshop = new CreativeTabArmourersWorkshop(CreativeTabs.getNextID(), LibModInfo.ID.toLowerCase());

    @EventHandler
    public void perInit(FMLPreInitializationEvent event) {
        ModLogger.log(String.format("Loading %s version %s.", LibModInfo.NAME, LibModInfo.MOD_VERSION));
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
    
    @NetworkCheckHandler
    public boolean checkRemoteVersions(Map<String, String> versions, Side side) {
        if (versions.containsKey(LibModInfo.ID)) {
            return true;
        }
        return false;
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
            if (!imcMessage.isStringMessage())
                continue;
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
