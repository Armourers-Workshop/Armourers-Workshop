package moe.plushie.armourers_workshop.common.lib;

public class LibModInfo {
    
    public static final String ID = "armourers_workshop";
    public static final String NAME = "Armourer's Workshop";
    public static final String MOD_VERSION = "@VERSION@";
    //public static final String MOD_VERSION = "1.12.2-0.49.0.397";
    public static final ReleaseType RELEASE_TYPE = ReleaseType.BETA;
    public static final String MC_VERSION = "1.12.2";
    public static final String CHANNEL = "arms-ws";
    public static final String DEPENDENCIES = "required-after:forge@[14.23.3.2694,);after:galacticraftcore;";
    public static final boolean DEVELOPMENT_VERSION = true;
    public static final String DOWNLOAD_URL = "https://minecraft.curseforge.com/projects/armourers-workshop/files";
    public static final String UPDATE_URL = "http://plushie.moe/app_update/minecraft_mods/armourers_workshop/update.json";

    public static final String PROXY_CLIENT_CLASS = "moe.plushie.armourers_workshop.proxies.ClientProxy";
    public static final String PROXY_COMMNON_CLASS = "moe.plushie.armourers_workshop.proxies.CommonProxy";
    public static final String GUI_FACTORY_CLASS = "moe.plushie.armourers_workshop.client.gui.config.ModGuiFactory";
    
    public static enum ReleaseType {
        PRE_APLHA,
        ALPHA,
        BETA,
        RC,
        RELEASED
    }
}
