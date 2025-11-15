package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.uikit.UIWindowManager;
import moe.plushie.armourers_workshop.compat.client.gui.AbstractMenuToast;

public class ContainerMenuToast<W extends ToastWindow> extends AbstractMenuToast {

    private final W window;
    private final UIWindowManager manager;

    public ContainerMenuToast(W window) {
        this.window = window;

        this.manager = new UIWindowManager();
        this.manager.addWindow(window);
        this.manager.init();
        this.manager.layout(screenSize());
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        manager.tick();
        manager.render(context, this::nop, this::nop, this::nop);
    }

    @Override
    public double duration() {
        return window.duration();
    }

    private void nop(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        // nop
    }
}
