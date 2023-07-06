package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.compatibility.client.gui.AbstractMenuScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

@Environment(EnvType.CLIENT)
public class SlotListView<M extends AbstractContainerMenu> extends UIView {

    protected final M menu;
    protected final DelegateScreen<M> screen;

    private boolean isReady = false;

    public SlotListView(M menu, Inventory inventory, CGRect frame) {
        super(frame);
        this.menu = menu;
        this.screen = new DelegateScreen<>(menu, inventory, Component.literal(""));
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        UIWindow window = window();
        if (window != null) {
            screen.setup(convertRectToView(bounds(), null), window.bounds());
            isReady = true;
        }
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        if (!isReady) {
            return;
        }
        int mouseX = (int) context.state().mouseX();
        int mouseY = (int) context.state().mouseY();
        CGPoint offset = screen.getContentOffset();
        context.saveGraphicsState();
        context.translateCTM(-offset.getX(), -offset.getY(), 0);
        screen.renderInView(this, 400, mouseX, mouseY, context.state().partialTicks(), context);
        context.restoreGraphicsState();
    }

    @Override
    public void mouseDown(UIEvent event) {
        CGPoint pt = locationInScreen(event);
        screen.mouseClicked(pt.x, pt.y, event.key());
    }

    @Override
    public void mouseUp(UIEvent event) {
        CGPoint pt = locationInScreen(event);
        screen.mouseReleased(pt.x, pt.y, event.key());
    }

    @Override
    public void removeFromSuperview() {
        super.removeFromSuperview();
        screen.removed();
    }

    public M getMenu() {
        return menu;
    }

    private CGPoint locationInScreen(UIEvent event) {
        CGPoint point = event.locationInWindow();
        UIWindow window = window();
        if (window != null) {
            CGRect frame = window.frame();
            return new CGPoint(point.x + frame.x, point.y + frame.y);
        }
        return point;
    }

    public static class DelegateScreen<M extends AbstractContainerMenu> extends AbstractMenuScreen<M> {

        private final Inventory inventory;

        public DelegateScreen(M menu, Inventory inventory, Component component) {
            super(menu, inventory, component);
            this.inventory = inventory;
            // yep, we need init it.
            this.init(Minecraft.getInstance(), 640, 480);
        }

        @Override
        public void onClose() {
            // ignore
        }

        public void setup(CGRect rect, CGRect bounds) {
            setContentSize(new CGSize(rect.width, rect.height));
            resize(Minecraft.getInstance(), (int) bounds.width, (int) bounds.height);
            setContentOffset(new CGPoint(rect.x, rect.y));
        }

        @Override
        public void renderLabels(CGGraphicsContext context, int mouseX, int mouseY) {
            // ignored
        }

        @Override
        public void slotClicked(Slot slot, int i, int j, ClickType clickType) {
            if (slot != null) {
                menu.clicked(slot.index, j, clickType, inventory.player);
            }
        }
    }
}
