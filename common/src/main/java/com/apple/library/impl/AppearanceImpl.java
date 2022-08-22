package com.apple.library.impl;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.init.ModTextures;

import java.util.HashMap;

public class AppearanceImpl {

    public static final UIColor TEXT_COLOR = UIColor.of(0x404040);

    public static final UIColor TEXT_BORDER_COLOR = UIColor.of(-6250336);
    public static final UIColor TEXT_FOCUSED_BORDER_COLOR = UIColor.WHITE;
    public static final UIColor TEXT_HIGHLIGHTED_COLOR = UIColor.BLUE;
    public static final UIColor TEXT_BACKGROUND_COLOR = UIColor.of(-16777216);
    public static final UIColor TEXT_CURSOR_COLOR = UIColor.of(-3092272);

    public static final UIColor SLIDER_TEXT_COLOR = UIColor.WHITE;
    public static final UIColor SLIDER_HIGHLIGHTED_TEXT_COLOR = UIColor.of(0xffffffa0);

    public static final UIImage BUTTON_IMAGE = ModTextures.defaultButtonImage();

    public static final UIImage TAB_BUTTON_LEFT_IMAGE = createTabButtonImages(0);
    public static final UIImage TAB_BUTTON_RIGHT_IMAGE = createTabButtonImages(1);

    private static UIImage createTabButtonImages(int alignment) {
        // we use special mapping tables.
        HashMap<Integer, CGPoint> offsets = new HashMap<>();
        offsets.put(UIControl.State.NORMAL, new CGPoint(0, 1));
        offsets.put(UIControl.State.HIGHLIGHTED, new CGPoint(1, 1));
        offsets.put(UIControl.State.SELECTED | UIControl.State.NORMAL, new CGPoint(0, 0));
        offsets.put(UIControl.State.SELECTED | UIControl.State.HIGHLIGHTED, new CGPoint(1, 0));
        int width = 26;
        int height = 30;
        int u = 2 * width * alignment;
        return UIImage.of(ModTextures.TABS).uv(u, 0).size(width, height).unzip(offsets::get).build();
    }
}
