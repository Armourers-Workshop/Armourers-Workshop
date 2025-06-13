package com.apple.library.impl;

import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.init.ModTextures;

public class AppearanceImpl {

    public static final UIColor DEFAULT_TEXT_COLOR = UIColor.of(0xff404040);

    public static final UIColor TEXT_BORDER_COLOR = UIColor.of(0xffa0a0a0);
    public static final UIColor TEXT_FOCUSED_BORDER_COLOR = UIColor.WHITE;
    public static final UIColor TEXT_HIGHLIGHTED_COLOR = UIColor.BLUE;
    public static final UIColor TEXT_BACKGROUND_COLOR = UIColor.of(0xff000000);
    public static final UIColor TEXT_CURSOR_COLOR = UIColor.of(0xffd0d0d0);

    public static final UIColor TREE_TEXT_COLOR = UIColor.WHITE;
    public static final UIColor TREE_HIGHLIGHTED_TEXT_COLOR = UIColor.of(0xffffffa0);
    public static final UIColor TREE_HIGHLIGHTED_BACKGROUND_COLOR = UIColor.of(0x44cccccc);
    public static final UIColor TREE_SELECTED_BACKGROUND_COLOR = UIColor.of(0x44ffff00);

    public static final UIColor MENU_NORMAL_TEXT_COLOR = UIColor.WHITE;
    public static final UIColor MENU_HIGHLIGHTED_TEXT_COLOR = UIColor.WHITE;
    public static final UIColor MENU_DISABLED_TEXT_COLOR = UIColor.of(0xff666666);
    public static final UIColor MENU_SEPARATOR_COLOR = UIColor.GRAY;

    public static final UIColor SLIDER_TEXT_COLOR = UIColor.WHITE;
    public static final UIColor SLIDER_HIGHLIGHTED_TEXT_COLOR = UIColor.of(0xffffffa0);

    public static final UIImage BUTTON_IMAGE = ModTextures.defaultButtonImage();
}
