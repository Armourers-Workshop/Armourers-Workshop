package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.uikit.UIWindowManager;
import moe.plushie.armourers_workshop.compatibility.client.gui.AbstractMenuToast;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ContainerMenuToast<W extends ToastWindow> extends AbstractMenuToast {

    private final W window;
    private final UIWindowManager manager;

    public ContainerMenuToast(W window) {

        this.window = window;

        this.manager = new UIWindowManager();
        this.manager.addWindow(window);
        this.manager.init();

        var screenSize = getScreenSize();
        this.manager.layout(screenSize.getWidth(), screenSize.getHeight());
    }

    @Override
    public void render(CGGraphicsContext context) {
        manager.tick();
        manager.render(context, this::none, this::none, this::none);
    }

    @Override
    public double getDuration() {
        return window.getDuration();
    }

    private void none(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
    }
}
