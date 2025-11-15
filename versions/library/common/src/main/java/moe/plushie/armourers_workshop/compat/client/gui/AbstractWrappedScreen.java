package moe.plushie.armourers_workshop.compat.client.gui;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.gui.widget.ContainerMenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.SlotListView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

@Available("[1.16, 1.22)")
@OnlyIn(Dist.CLIENT)
public class AbstractWrappedScreen extends AbstractWrappedScreenImpl {

    public AbstractWrappedScreen(Screen screen) {
        super(screen);
    }

    public static boolean shouldRenderExtendScreen(Screen screen) {
        // The delegate screen should never render extend.
        if (screen instanceof SlotListView.DelegateScreen) {
            return false;
        }
        // we respect users settings.
        if (screen instanceof ContainerMenuScreen<?, ?> screen1) {
            return screen1.shouldRenderExtendScreen();
        }
        return true;
    }

    @Override
    protected void init() {
        super.init();
        screen.init(Minecraft.getInstance(), width, height);
        _updateContentSize();
    }

    @Override
    public void tick() {
        super.tick();
        screen.tick();
    }

    @Override
    public void removed() {
        super.removed();
        screen.removed();
    }

    @Override
    public void resize(Minecraft minecraft, int i, int j) {
        screen.resize(minecraft, i, j);
        _updateContentSize();
    }

    @Override
    public boolean keyPressed(int key, int i, int j) {
        return screen.keyPressed(key, i, j);
    }

    @Override
    public boolean keyReleased(int key, int i, int j) {
        return screen.keyReleased(key, i, j);
    }

    @Override
    public boolean charTyped(char ch, int i) {
        return screen.charTyped(ch, i);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return screen.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return screen.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        return screen.mouseDragged(d, e, i, f, g);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        screen.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double d, double e) {
        return screen.isMouseOver(d, e);
    }

    @Override
    public boolean isPauseScreen() {
        return screen.isPauseScreen();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return screen.shouldCloseOnEsc();
    }

    protected void _updateContentSize() {
        width = this.screen.width;
        height = this.screen.height;
    }
}
