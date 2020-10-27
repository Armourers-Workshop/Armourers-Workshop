package moe.plushie.armourers_workshop.client.lib;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LibGuiResources {

    private static final String PREFIX_TEXTURE = LibModInfo.ID + ":textures/gui/";
    private static final String PREFIX_JSON = LibModInfo.ID + ":gui/";

    // Common
    public static final String COMMON = PREFIX_TEXTURE + "common.png";
    public static final String PLAYER_INVENTORY = PREFIX_TEXTURE + "player_inventory.png";
    public static final String SKIN_PREVIEW = PREFIX_TEXTURE + "skin-preview.png";

    // Controls
    public static final String CONTROL_BUTTONS = PREFIX_TEXTURE + "_controls/buttons.png";
    public static final String CONTROL_HELP = PREFIX_TEXTURE + "_controls/help.png";
    public static final String CONTROL_LIST = PREFIX_TEXTURE + "_controls/list.png";
    public static final String CONTROL_SCROLLBAR = PREFIX_TEXTURE + "_controls/scrollbar.png";
    public static final String CONTROL_SKIN_PANEL = PREFIX_TEXTURE + "_controls/skin-panel.png";
    public static final String CONTROL_SLIDER_HUE = PREFIX_TEXTURE + "_controls/slider-hue.png";
    public static final String CONTROL_TABS = PREFIX_TEXTURE + "_controls/tabs.png";
    public static final String CONTROL_TAB_ICONS = PREFIX_TEXTURE + "_controls/tab_icons.png";
    public static final String CONTROL_RATING = PREFIX_TEXTURE + "_controls/rating.png";

    // GUIs
    public static final String GUI_ADVANCED_SKIN_BUILDER = PREFIX_TEXTURE + "advanced_skin_builder/advanced-skin-builder.png";
    public static final String GUI_ARMOURER = PREFIX_TEXTURE + "armourer/armourer.png";
    public static final String GUI_COLOUR_MIXER = PREFIX_TEXTURE + "colour_mixer/colour-mixer.png";
    public static final String GUI_DYE_TABLE = PREFIX_TEXTURE + "dye_table/dye-table.png";
    public static final String GUI_GLOBAL_LIBRARY = PREFIX_TEXTURE + "global_library/global-library.png";
    public static final String GUI_GUIDE_BOOK = PREFIX_TEXTURE + "guide_book/guide-book.png";
    public static final String GUI_GUIDE_BOOK_PAGES = PREFIX_TEXTURE + "guide_book/guide-book-page.png";
    public static final String GUI_HOLOGRAM_PROJECTOR = PREFIX_TEXTURE + "hologram_projector/hologram-projector.png";
    public static final String GUI_OUTFIT_MAKER = PREFIX_TEXTURE + "outfit_maker/outfit-maker.png";
    public static final String GUI_SKIN_LIBRARY = PREFIX_TEXTURE + "skin_library/armour-library.png";
    public static final String GUI_SKINNING_TABLE = PREFIX_TEXTURE + "skinning_table/skinning-table.png";
    public static final String GUI_WARDROBE_1 = PREFIX_TEXTURE + "wardrobe/wardrobe-1.png";
    public static final String GUI_WARDROBE_2 = PREFIX_TEXTURE + "wardrobe/wardrobe-2.png";

    // JSONs
    public static final String JSON_WARDROBE = PREFIX_JSON + "wardrobe.json";

    public class Controls {

        private static final String PREFIX = "inventory." + LibModInfo.ID + ":common.";

        public static final String BUTTON_CLOSE = PREFIX + "button.close";
        public static final String BUTTON_CANCEL = PREFIX + "button.cancel";
        public static final String BUTTON_OK = PREFIX + "button.ok";
        public static final String BUTTON_EDIT = PREFIX + "button.edit";
        public static final String BUTTON_PREVIOUS = PREFIX + "button.previous";
        public static final String BUTTON_NEXT = PREFIX + "button.next";
    }
}
