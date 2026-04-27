package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.compat.client.gui.AbstractMenuScreen;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractContainerInput;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

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
        var window = window();
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
        var param = context.param();
        var offset = screen.contentOffset();
        context.saveGraphicsState();
        context.translateCTM(-offset.x, -offset.y, 0);
        screen.renderInView(this, 400, (int) param.mouseX(), (int) param.mouseY(), param.partialTick(), context);
        context.restoreGraphicsState();
    }

    @Override
    public void mouseDown(UIEvent event) {
        var point = locationInScreen(event);
        screen.mouseClicked(point, event);
    }

    @Override
    public void mouseUp(UIEvent event) {
        var point = locationInScreen(event);
        screen.mouseReleased(point, event);
    }

    @Override
    public void removeFromSuperview() {
        super.removeFromSuperview();
        screen.removed();
    }

    public M menu() {
        return menu;
    }

    private CGPoint locationInScreen(UIEvent event) {
        var point = event.locationInWindow();
        var window = window();
        if (window != null) {
            var frame = window.frame();
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
        public void slotClicked(Slot slot, int slotId, int buttonNum, AbstractContainerInput input) {
            if (slot != null) {
                menu.clicked(slot, slotId, buttonNum, input, inventory.player);
            }
        }
    }
}
