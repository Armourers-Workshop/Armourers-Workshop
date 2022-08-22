package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.ext.OpenPoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class SlotListView<M extends AbstractContainerMenu> extends UIView {

    protected final M menu;
    protected final DelegateScreen<M> screen;

    private boolean isReady = false;

    public SlotListView(M menu, Inventory inventory, CGRect frame) {
        super(frame);
        this.menu = menu;
        this.screen = new DelegateScreen<>(menu, inventory, TextComponent.EMPTY);
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
        screen.render(context.poseStack, context.mouseX, context.mouseY, context.partialTicks);
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

    public static class DelegateScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {

        public DelegateScreen(M abstractContainerMenu, Inventory inventory, Component component) {
            super(abstractContainerMenu, inventory, component);
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
        public void render(PoseStack poseStack, int i, int j, float f) {
            OpenPoseStack modelViewStack = RenderSystem.getResolvedModelViewStack();
            poseStack.pushPose();
            poseStack.translate(-leftPos, -topPos, 0);
            modelViewStack.pushPose();
            modelViewStack.translate(0, 0, 400);
            super.render(poseStack, i, j, f);
            modelViewStack.popPose();
            super.renderTooltip(poseStack, i, j);
            poseStack.popPose();
        }

        @Override
        protected void renderBg(PoseStack poseStack, float f, int i, int j) {
        }

        @Override
        protected void renderLabels(PoseStack poseStack, int i, int j) {
        }

        @Override
        protected void slotClicked(Slot slot, int i, int j, ClickType clickType) {
            if (slot != null) {
                menu.clicked(slot.index, j, clickType, inventory.player);
            }
        }
    }
}
