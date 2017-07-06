package riskyken.armourersWorkshop.client.lib;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class LibGuiResources {
    
    private static final String PREFIX_RESOURCE = LibModInfo.ID.toLowerCase() + ":textures/gui/";
    
    public static final String MANNEQUIN = PREFIX_RESOURCE + "mannequin.png";
    public static final String MANNEQUIN_TABS = PREFIX_RESOURCE + "mannequinTabs.png";
    public static final String ARMOURER = PREFIX_RESOURCE + "armourer.png";
    public static final String ARMOURER_TABS = PREFIX_RESOURCE + "armourerTabs.png";
    public static final String HOLOGRAM_PROJECTOR = PREFIX_RESOURCE + "hologramProjector.png";
    public static final String HOLOGRAM_PROJECTOR_TABS = PREFIX_RESOURCE + "hologramProjectorTabs.png";
}
