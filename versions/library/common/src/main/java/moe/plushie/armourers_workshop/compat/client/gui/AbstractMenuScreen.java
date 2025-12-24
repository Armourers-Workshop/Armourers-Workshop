package moe.plushie.armourers_workshop.compat.client.gui;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGSize;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractCharacterEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractKeyEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractMouseButtonEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractMouseEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractMouseWheelEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Available("[1.16, 1.26)")
@OnlyIn(Dist.CLIENT)
public class AbstractMenuScreen<T extends AbstractContainerMenu> extends AbstractMenuScreenImpl<T> {

    public AbstractMenuScreen(T menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    public void onClose() {
        super.onClose();
    }

    public CGPoint contentOffset() {
        return new CGPoint(leftPos, topPos);
    }

    public void setContentOffset(CGPoint offset) {
        leftPos = (int) offset.x;
        topPos = (int) offset.y;
    }

    public CGSize contentSize() {
        return new CGSize(imageWidth, imageHeight);
    }

    public void setContentSize(CGSize size) {
        imageWidth = (int) size.width;
        imageHeight = (int) size.height;
    }

    public CGSize screenSize() {
        return new CGSize(width, height);
    }

    public void setScreenSize(CGSize size) {
        width = (int) size.width;
        height = (int) size.height;
    }

    public boolean keyPressed(AbstractKeyEvent event) {
        return super.keyPressed(event.code, event.modifiers, event.scancode);
    }

    public boolean keyReleased(AbstractKeyEvent event) {
        return super.keyPressed(event.code, event.modifiers, event.scancode);
    }

    public boolean charTyped(AbstractCharacterEvent event) {
        return super.charTyped(event.ch, event.modifiers);
    }

    public boolean mouseClicked(AbstractMouseButtonEvent event, boolean bl) {
        return super.mouseClicked(event.mouseX, event.mouseY, event.button);
    }

    public boolean mouseReleased(AbstractMouseButtonEvent event) {
        return super.mouseReleased(event.mouseX, event.mouseY, event.button);
    }

    public void mouseMoved(AbstractMouseEvent event) {
        super.mouseMoved(event.mouseX, event.mouseY);
    }

    public boolean mouseScrolled(AbstractMouseWheelEvent event) {
        return super.mouseScrolled(event.mouseX, event.mouseY, event.deltaX, event.deltaY);
    }

    public boolean hasClickedOutside(int x, int y, AbstractMouseButtonEvent event) {
        return super.hasClickedOutside(event.mouseX, event.mouseY, x, y, event.button);
    }

    @Override
    public final boolean keyPressed(int key, int i, int j) {
        return keyPressed(new AbstractKeyEvent(key, i, j));
    }

    @Override
    public final boolean keyReleased(int key, int i, int j) {
        return keyReleased(new AbstractKeyEvent(key, i, j));
    }

    @Override
    public final boolean charTyped(char ch, int i) {
        return charTyped(new AbstractCharacterEvent(ch, i));
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return mouseClicked(new AbstractMouseButtonEvent(mouseX, mouseY, button), false);
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        return mouseReleased(new AbstractMouseButtonEvent(mouseX, mouseY, button));
    }

    @Override
    public final void mouseMoved(double mouseX, double mouseY) {
        mouseMoved(new AbstractMouseEvent(mouseX, mouseY));
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return mouseScrolled(new AbstractMouseWheelEvent(mouseX, mouseY, deltaX, deltaY));
    }

    @Override
    public final boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int button) {
        return hasClickedOutside(x, y, new AbstractMouseButtonEvent(mouseX, mouseY, button));
    }
}

