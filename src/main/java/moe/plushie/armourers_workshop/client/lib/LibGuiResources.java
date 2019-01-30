package moe.plushie.armourers_workshop.client.lib;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LibGuiResources {

    private static final String PREFIX_RESOURCE = LibModInfo.ID + ":textures/gui/";

    public static final String MANNEQUIN = PREFIX_RESOURCE + "mannequin.png";
    public static final String MANNEQUIN_TABS = PREFIX_RESOURCE + "mannequin-tabs.png";
    public static final String ARMOURER = PREFIX_RESOURCE + "armourer.png";
    public static final String ARMOURER_TABS = PREFIX_RESOURCE + "armourer-tabs.png";
    public static final String WARDROBE = PREFIX_RESOURCE + "wardrobe.png";
    public static final String WARDROBE_PALETTE = PREFIX_RESOURCE + "wardrobe-palette.png";
    public static final String WARDROBE_TABS = PREFIX_RESOURCE + "wardrobe-tabs.png";
    public static final String HOLOGRAM_PROJECTOR = PREFIX_RESOURCE + "hologram-projector.png";
    public static final String HOLOGRAM_PROJECTOR_TABS = PREFIX_RESOURCE + "hologram-projector-tabs.png";
    public static final String SKINNABLE = PREFIX_RESOURCE + "skinnable.png";
    public static final String SKIN_PREVIEW = PREFIX_RESOURCE + "skin-preview.png";
    public static final String OUTFIT_MAKER = PREFIX_RESOURCE + "outfit-maker.png";
}
