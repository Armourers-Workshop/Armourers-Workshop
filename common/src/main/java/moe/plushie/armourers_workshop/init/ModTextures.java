package moe.plushie.armourers_workshop.init;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

import java.util.HashMap;

public class ModTextures {

    public static final OpenResourceKey WARDROBE_1 = res("textures/gui/wardrobe/wardrobe-1.png");
    public static final OpenResourceKey WARDROBE_2 = res("textures/gui/wardrobe/wardrobe-2.png");

    public static final OpenResourceKey SKINNING_TABLE = res("textures/gui/skinning_table/skinning-table.png");
    public static final OpenResourceKey DYE_TABLE = res("textures/gui/dye_table/dye-table.png");
    public static final OpenResourceKey SKIN_LIBRARY = res("textures/gui/skin_library/armour-library.png");
    public static final OpenResourceKey GLOBAL_SKIN_LIBRARY = res("textures/gui/global_library/global-library.png");
    public static final OpenResourceKey COLOR_MIXER = res("textures/gui/colour_mixer/colour-mixer.png");
    public static final OpenResourceKey OUTFIT_MAKER = res("textures/gui/outfit_maker/outfit-maker.png");
    public static final OpenResourceKey ARMOURER = res("textures/gui/armourer/armourer.png");
    public static final OpenResourceKey ADVANCED_SKIN_BUILDER = res("textures/gui/advanced_skin_builder/advanced-skin-builder.png");

    public static final OpenResourceKey TABS = res("textures/gui/controls/tabs.png");
    public static final OpenResourceKey COMMON = res("textures/gui/common.png");
    public static final OpenResourceKey WIDGETS = res("textures/gui/widgets.png");
    public static final OpenResourceKey MENUS = res("textures/gui/controls/menus.png");
    public static final OpenResourceKey LIST = res("textures/gui/controls/list.png");
    public static final OpenResourceKey RATING = res("textures/gui/controls/rating.png");
    public static final OpenResourceKey TAB_ICONS = res("textures/gui/controls/tab_icons.png");
    public static final OpenResourceKey HUE = res("textures/gui/controls/slider-hue.png");
    public static final OpenResourceKey TOASTS = res("textures/gui/controls/toasts.png");

    public static final OpenResourceKey BUTTONS = res("textures/gui/controls/buttons.png");
    public static final OpenResourceKey HELP = res("textures/gui/controls/help.png");

    public static final OpenResourceKey PLAYER_INVENTORY = res("textures/gui/player_inventory.png");

    public static final OpenResourceKey CUBE = res("textures/armour/cube.png");
    public static final OpenResourceKey LIGHTING_CUBE = res("textures/armour/glowcube.png");
    public static final OpenResourceKey EARTH = res("textures/blockentity/global-skin-library.png");

    public static final OpenResourceKey MANNEQUIN_DEFAULT = res("textures/entity/mannequin.png");
    public static final OpenResourceKey MANNEQUIN_HIGHLIGHT = res("textures/entity/mannequin_h.png");
    public static final OpenResourceKey HORSE_DEFAULT = res("textures/entity/horse.png");
    public static final OpenResourceKey BOAT_DEFAULT = res("textures/entity/boat.png");
    public static final OpenResourceKey MINECART_DEFAULT = res("textures/entity/minecart.png");

    public static final OpenResourceKey GUI_PREVIEW = res("textures/gui/skin-preview.png");
    public static final OpenResourceKey SKIN_PANEL = res("textures/gui/controls/skin-panel.png");
    public static final OpenResourceKey SCROLLBAR = res("textures/gui/controls/scrollbar.png");

    public static final OpenResourceKey MARKERS = res("textures/blockentity/markers.png");
    public static final OpenResourceKey GUIDES = res("textures/block/guide.png");

    public static final OpenResourceKey BLOCK_CUBE = res("textures/block/colourable/colourable.png");
    public static final OpenResourceKey BLOCK_CUBE_GLASS = res("textures/block/colourable/colourable-glass.png");

    private static OpenResourceKey res(String name) {
        return ModConstants.key(name);
    }

    public static UIImage defaultWindowImage() {
        return UIImage.of(COMMON).fixed(128, 128).clip(4, 4, 4, 4).build();
    }

    public static UIImage defaultButtonImage() {
        var offsets = new HashMap<Integer, CGPoint>();
        offsets.put(UIControl.State.DISABLED, new CGPoint(0, 0));
        offsets.put(UIControl.State.NORMAL, new CGPoint(0, 1));
        offsets.put(UIControl.State.HIGHLIGHTED, new CGPoint(0, 2));
        return UIImage.of(WIDGETS).uv(0, 46).fixed(200, 20).clip(4, 4, 4, 4).unzip(offsets::get).build();
    }

    public static UIImage defaultButtonImage(float u, float v) {
        return buttonImage(ModTextures.BUTTONS, u, v, 16, 16);
    }

    public static UIImage buttonImage(OpenResourceKey texture, float u, float v, float width, float height) {
        var offsets = new HashMap<Integer, CGPoint>();
        offsets.put(UIControl.State.NORMAL, new CGPoint(0, 0));
        offsets.put(UIControl.State.HIGHLIGHTED, new CGPoint(1, 0));
        offsets.put(UIControl.State.SELECTED | UIControl.State.NORMAL, new CGPoint(2, 0));
        offsets.put(UIControl.State.SELECTED | UIControl.State.HIGHLIGHTED, new CGPoint(3, 0));
        // a strange design, there are multiple paths:
        // 1. normal + highlighted + selected + selected and highlighted
        // 2. normal + highlighted + disabled
        offsets.put(UIControl.State.DISABLED, new CGPoint(3, 0));
        return UIImage.of(texture).uv(u, v).fixed(width, height).unzip(offsets::get).build();
    }

    public static UIImage iconImage(float u, float v, float width, float height, OpenResourceKey resource) {
        var offsets = new HashMap<Integer, CGPoint>();
        offsets.put(UIControl.State.NORMAL, new CGPoint(0, 0));
        offsets.put(UIControl.State.HIGHLIGHTED, new CGPoint(1, 0));
        offsets.put(UIControl.State.DISABLED, new CGPoint(2, 0));
        return UIImage.of(resource).uv(u, v).fixed(width, height).unzip(offsets::get).build();
    }

    public static UIImage helpButtonImage() {
        var offsets = new HashMap<Integer, CGPoint>();
        offsets.put(UIControl.State.NORMAL, new CGPoint(0, 0));
        offsets.put(UIControl.State.HIGHLIGHTED, new CGPoint(1, 0));
        return UIImage.of(HELP).fixed(7, 8).unzip(offsets::get).build();
    }
}
