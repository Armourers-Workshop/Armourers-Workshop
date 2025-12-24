package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.client.gui.screens.Screen;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.uikit.UIEvent;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.client.gui.screens.Screen;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, 1.26)")
@Extension
public class EventProvider {

    public static boolean mouseClicked(@This Screen screen, CGPoint point, UIEvent event) {
        var mouse = event.mouse();
        if (mouse != null) {
            return screen.mouseClicked(point.x, point.y, mouse.button());
        }
        return false;
    }

    public static boolean mouseReleased(@This Screen screen, CGPoint point, UIEvent event) {
        var mouse = event.mouse();
        if (mouse != null) {
            return screen.mouseReleased(point.x, point.y, mouse.button());
        }
        return false;
    }

}
