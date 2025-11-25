package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIWindow;
import com.apple.library.uikit.UIWindowManager;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.gui.AbstractMenuScreen;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractCharacterEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractKeyEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractMouseButtonEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractMouseEvent;
import moe.plushie.armourers_workshop.compat.client.gui.event.AbstractMouseWheelEvent;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class ContainerMenuScreen<M extends AbstractContainerMenu, W extends UIWindow> extends AbstractMenuScreen<M> {

    private final W window;
    private final MenuWindow<?> menuWindow;
    private final UIWindowManager manager;

    public ContainerMenuScreen(W window, M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);

        this.window = window;
        this.menuWindow = Objects.safeCast(window, MenuWindow.class);

        this.manager = new UIWindowManager();
        this.manager.addWindow(window);
        this.manager.init();
    }

    @Override
    public void init() {
        manager.layout(screenSize());
        var rect = window.bounds();
        setContentSize(new CGSize(rect.width, rect.height));
        super.init();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        menuWindow.screenWillTick();
    }

    @Override
    public void removed() {
        super.removed();
        manager.deinit();
    }

    @Override
    public void render(CGGraphicsContext context, int mouseX, int mouseY, float partialTicks) {
        manager.tick();
        manager.render(context, this::_render, this::_renderBackground, this::_renderTooltip);
    }

    @Override
    public void renderLabels(CGGraphicsContext context, int mouseX, int mouseY) {
        // ignored
    }

    @Override
    public boolean keyPressed(AbstractKeyEvent event) {
        return manager.keyDown(event, this::_keyPressed);
    }

    @Override
    public boolean keyReleased(AbstractKeyEvent event) {
        return manager.keyUp(event, this::_keyReleased);
    }

    @Override
    public boolean charTyped(AbstractCharacterEvent event) {
        return manager.charTyped(event, this::_charTyped);
    }

    @Override
    public boolean mouseClicked(AbstractMouseButtonEvent event, boolean bl) {
        return manager.mouseDown(event, bl, this::_mouseClicked);
    }

    @Override
    public boolean mouseReleased(AbstractMouseButtonEvent event) {
        return manager.mouseUp(event, this::_mouseReleased);
    }

    @Override
    public void mouseMoved(AbstractMouseEvent event) {
        manager.mouseMoved(event, this::_mouseMoved);
    }

    @Override
    public boolean mouseScrolled(AbstractMouseWheelEvent event) {
        return manager.mouseWheel(event, this::_mouseScrolled);
    }

    @Override
    public boolean hasClickedOutside(int x, int y, AbstractMouseButtonEvent event) {
        return !manager.mouseIsInside(event);
    }

    @Override
    public void slotClicked(Slot slot, int slotIndex, int j, ClickType clickType) {
        super.slotClicked(slot, slotIndex, j, clickType);
        menuWindow.menuDidChange();
    }

    @Override
    public boolean changeFocus(boolean bl) {
        return manager.changeKeyView(bl);
    }

    public boolean shouldRenderExtendScreen() {
        if (menuWindow != null) {
            return menuWindow.shouldRenderExtendScreen();
        }
        return false;
    }

    protected void _render(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        super.render(context, mouseX, mouseY, partialTicks);
    }

    protected void _renderTooltip(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        context.saveGraphicsState();
        context.translateCTM(0, 0, 400);
        renderTooltip(context, mouseX, mouseY);
        context.restoreGraphicsState();
    }

    protected void _renderBackground(int mouseX, int mouseY, float partialTicks, CGGraphicsContext context) {
        // draw bg
        if (menuWindow != null && menuWindow.shouldRenderBackground()) {
            renderBackground(context);
        }
    }

    protected boolean _charTyped(AbstractCharacterEvent event) {
        super.charTyped(event);
        return true;
    }

    protected boolean _keyPressed(AbstractKeyEvent event) {
        // when active input first responder, only the shortcut key events is allowed.
        if (manager.isTextEditing() && !_editingPassKey(event)) {
            return false;
        }
        return super.keyPressed(event);
    }

    protected boolean _keyReleased(AbstractKeyEvent event) {
        // when active input first responder, only the shortcut key events is allowed.
        if (manager.isTextEditing() && !_editingPassKey(event)) {
            return false;
        }
        return super.keyReleased(event);
    }

    protected boolean _mouseClicked(AbstractMouseButtonEvent event, boolean bl) {
        return super.mouseClicked(event, bl);
    }

    protected boolean _mouseReleased(AbstractMouseButtonEvent event) {
        return super.mouseReleased(event);
    }

    protected boolean _mouseMoved(AbstractMouseEvent event) {
        super.mouseMoved(event);
        return true;
    }

    protected boolean _mouseScrolled(AbstractMouseWheelEvent event) {
        return super.mouseScrolled(event);
    }

    protected boolean _editingPassKey(AbstractKeyEvent event) {
        return switch (event.code()) {
            case GLFW.GLFW_KEY_ESCAPE -> true;
            case GLFW.GLFW_KEY_TAB -> true;
            default -> false;
        };
    }
}
