package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import me.sagesse.minecraft.client.gui.ContainerMenuScreen;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

@Environment(value = EnvType.CLIENT)
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
        screen.render(context.poseStack.cast(), context.mouseX, context.mouseY, context.partialTicks);
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

    public static class DelegateScreen<M extends AbstractContainerMenu> extends ContainerMenuScreen<M> {

        private final Inventory inventory;

        public DelegateScreen(M abstractContainerMenu, Inventory inventory, Component component) {
            super(abstractContainerMenu, inventory, component);
            this.inventory = inventory;
        }

        @Override
        public void onClose() {
        }

        public void setup(CGRect rect, CGRect bounds) {
            imageWidth = rect.width;
            imageHeight = rect.height;
            init(Minecraft.getInstance(), bounds.width, bounds.height);
            leftPos = rect.x;
            topPos = rect.y;
        }

        @Override
        public void render(IPoseStack poseStack, int i, int j, float f) {
            poseStack.pushPose();
            poseStack.translate(-leftPos, -topPos, 0);

            IPoseStack modelViewStack = RenderSystem.getExtendedModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.translate(0, 0, 400);
            RenderSystem.applyModelViewMatrix();
            super.render(poseStack, i, j, f);
            modelViewStack.popPose();
            RenderSystem.applyModelViewMatrix();

            super.renderTooltip(poseStack, i, j);
            poseStack.popPose();
        }

        @Override
        protected void renderBg(IPoseStack poseStack, float f, int i, int j) {
        }

        @Override
        protected void renderLabels(IPoseStack poseStack, int i, int j) {
        }

        @Override
        protected void slotClicked(Slot slot, int i, int j, ClickType clickType) {
            if (slot != null) {
                menu.clicked(slot.index, j, clickType, inventory.player);
            }
        }
    }
}
