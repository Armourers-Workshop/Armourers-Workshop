package com.apple.library.impl;

import com.apple.library.uikit.UIFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public interface FontImpl {

    UIFont SYSTEM_FONT = new UIFont(Minecraft.getInstance().font, 9);

    Font font();
}
