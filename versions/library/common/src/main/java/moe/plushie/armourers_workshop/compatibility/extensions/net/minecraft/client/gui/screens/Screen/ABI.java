package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.gui.screens.Screen;

import com.apple.library.coregraphics.CGGraphicsContext;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.gui.AbstractGraphicsRenderer;
import net.minecraft.client.gui.screens.Screen;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, )")
@Extension
public class ABI {

    public static void render(@This Screen screen, CGGraphicsContext context, int mouseX, int mouseY, float partialTicks) {
        screen.render(AbstractGraphicsRenderer.of(context), mouseX, mouseY, partialTicks);
    }
}
