package com.apple.library.impl;

import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.init.ModTextures;

public class AppearanceImpl {

    public static final UIColor TEXT_COLOR = UIColor.of(0x404040);

    public static final UIColor TEXT_BORDER_COLOR = UIColor.of(-6250336);
    public static final UIColor TEXT_FOCUSED_BORDER_COLOR = UIColor.WHITE;
    public static final UIColor TEXT_HIGHLIGHTED_COLOR = UIColor.BLUE;
    public static final UIColor TEXT_BACKGROUND_COLOR = UIColor.of(-16777216);
    public static final UIColor TEXT_CURSOR_COLOR = UIColor.of(-3092272);

    public static final UIColor TREE_TEXT_COLOR = UIColor.WHITE;
    public static final UIColor TREE_HIGHLIGHTED_TEXT_COLOR = new UIColor(0xffffa0);
    public static final UIColor TREE_HIGHLIGHTED_BACKGROUND_COLOR = new UIColor(0x44cccccc, true);
    public static final UIColor TREE_SELECTED_BACKGROUND_COLOR = new UIColor(0x44ffff00, true);

    public static final UIColor SLIDER_TEXT_COLOR = UIColor.WHITE;
    public static final UIColor SLIDER_HIGHLIGHTED_TEXT_COLOR = UIColor.of(0xffffffa0);

    public static final UIImage BUTTON_IMAGE = ModTextures.defaultButtonImage();
}
