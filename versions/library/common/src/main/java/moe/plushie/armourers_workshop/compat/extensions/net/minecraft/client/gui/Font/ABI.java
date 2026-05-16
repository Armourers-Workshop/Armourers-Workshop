package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.gui.Font;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[16, 26)")
public class ABI {

    public static Style componentStyleAtWidth(@This Font font, FormattedCharSequence text, int width) {
        return font.getSplitter().componentStyleAtWidth(text, width);
    }

    public static void splitLines(@This Font font, String text, int maxWidth, Style style, boolean backwards, StringSplitter.LinePosConsumer consumer) {
        font.getSplitter().splitLines(text, maxWidth, style, backwards, consumer);
    }
}
