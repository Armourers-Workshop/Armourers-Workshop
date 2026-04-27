package moe.plushie.armourers_workshop.compat.client.gui;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

@Available("[21, 26)")
@OnlyIn(Dist.CLIENT)
public class AbstractWrappedScreenImpl extends Screen {

    protected final Screen screen;

    public AbstractWrappedScreenImpl(Screen screen) {
        super(screen.getTitle());
        this.screen = screen;
    }

    @Override
    public void added() {
        super.added();
        screen.added();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        screen.render(guiGraphics, i, j, f);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return screen.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }
}
