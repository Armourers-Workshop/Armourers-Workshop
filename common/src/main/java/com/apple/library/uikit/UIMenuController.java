package com.apple.library.uikit;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.impl.InvokerResult;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class UIMenuController {

    private static final UIMenuController INSTANCE = new UIMenuController();

    private MenuPopoverView popoverView;

    private CGAffineTransform transform = CGAffineTransform.IDENTITY;

    private Collection<UIMenuItem> menuItems;

    public static UIMenuController getInstance() {
        return INSTANCE;
    }

    public void showMenu(UIView fromView, CGPoint fromPoint) {
        UIWindow window = fromView.window();
        if (window == null) {
            return;
        }
        MenuListView listView = new MenuListView(menuItems());
        CGSize size = listView.sizeThatFits(window.bounds().size());
        listView.setBounds(new CGRect(CGPoint.ZERO, size));
        listView.setAutoresizingMask(UIView.AutoresizingMask.flexibleRightMargin | UIView.AutoresizingMask.flexibleBottomMargin);
        listView.setContents(UIImage.of(ModTextures.MENUS).uv(0, 0).fixed(44, 44).clip(4, 4, 4, 4).build());
        listView.setTransform(transform());
        CGSize size1 = size.applying(transform());
        CGPoint center = fromView.convertPointToView(fromPoint, window).copy();
        if (center.x + size1.width > window.bounds().getMaxX()) {
            center.x -= size1.width;
        }
        center.x += size1.width / 2;
        center.y += size1.height / 2;
        listView.setCenter(center);
        MenuPopoverView popoverView = new MenuPopoverView();
        popoverView.setBackgroundColor(null);
        popoverView.setContentView(listView);
        popoverView.showInView(fromView);
        this.popoverView = popoverView;
    }

    public void dismissMenu() {
        if (this.popoverView != null) {
            this.popoverView.dismiss();
        }
    }

    public Collection<UIMenuItem> menuItems() {
        return menuItems;
    }

    public void setMenuItems(Collection<UIMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public CGAffineTransform transform() {
        return transform;
    }

    public void setTransform(CGAffineTransform transform) {
        this.transform = transform;
    }

    protected static class MenuCell extends UIButton {

        private final UIMenuItem menuItem;

        public MenuCell(UIMenuItem menuItem) {
            super(CGRect.ZERO);
            this.menuItem = menuItem;
            this.titleView().setShadowColor(null);
            this.setTitle(menuItem.title(), State.ALL);
            this.setTitleColor(AppearanceImpl.MENU_NORMAL_TEXT_COLOR, State.NORMAL);
            this.setTitleColor(AppearanceImpl.MENU_HIGHLIGHTED_TEXT_COLOR, State.HIGHLIGHTED);
            this.setTitleColor(AppearanceImpl.MENU_DISABLED_TEXT_COLOR, State.DISABLED);
            this.setBackgroundImage(UIImage.of(ModTextures.MENUS).uv(0, 44).fixed(44, 22).clip(4, 4, 4, 4).build(), State.HIGHLIGHTED);
            this.setHorizontalAlignment(NSTextAlignment.Horizontal.LEFT);
            this.setContentEdgeInsets(new UIEdgeInsets(4, 8, 4, 8));
            this.setEnabled(menuItem.isEnabled());
        }

        @Override
        public void mouseDown(UIEvent event) {
            super.mouseDown(event);
            if (menuItem != null) {
                menuItem.perform(event);
            }
            MenuPopoverView popoverView = ObjectUtils.safeCast(window(), MenuPopoverView.class);
            if (popoverView != null) {
                popoverView.dismiss();
            }
        }
    }

    protected static class MenuSeparatorView extends UIView {

        public MenuSeparatorView() {
            super(new CGRect(0, 0, 16, 7));
            UIView lineView = new UIView(bounds().insetBy(3, 5, 3.5f, 5));
            lineView.setBackgroundColor(AppearanceImpl.MENU_SEPARATOR_COLOR);
            lineView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
            addSubview(lineView);
        }

        @Override
        public CGSize sizeThatFits(CGSize size) {
            return new CGSize(16, 7);
        }
    }

    protected static class MenuListView extends UIScrollView {

        private final AtomicInteger lastGroup = new AtomicInteger();
        private final ArrayList<UIView> contentCells = new ArrayList<>();

        public MenuListView(Collection<UIMenuItem> menuItems) {
            super(CGRect.ZERO);
            this.setContentInsets(new UIEdgeInsets(4, 4, 4, 4));
            menuItems.stream().sorted(Comparator.comparingInt(UIMenuItem::group)).forEachOrdered(menuItem -> {
                // add separator when groups changed.
                if (!contentCells.isEmpty() && lastGroup.get() != menuItem.group()) {
                    UIView separatorView = new MenuSeparatorView();
                    addSubview(separatorView);
                    contentCells.add(separatorView);
                }
                UIView cell = new MenuCell(menuItem);
                addSubview(cell);
                contentCells.add(cell);
                lastGroup.set(menuItem.group());
            });
        }

        @Override
        public void mouseUp(UIEvent event) {
        }

        @Override
        public void mouseMoved(UIEvent event) {
        }

        @Override
        public void mouseDown(UIEvent event) {
        }

        @Override
        public void sizeToFit() {
            UIEdgeInsets edg = contentInsets();
            float x = edg.left;
            float y = edg.top;
            float maxWidth = 0;
            for (UIView cell : contentCells) {
                cell.sizeToFit();
                CGSize size = cell.bounds().size();
                cell.setFrame(new CGRect(x, y, size.width, size.height));
                y += size.height;
                maxWidth = Math.max(maxWidth, size.width);
            }
            for (UIView cell : contentCells) {
                CGRect frame = cell.frame();
                cell.setFrame(new CGRect(frame.x, frame.y, maxWidth, frame.height));
            }
            y += edg.bottom;
            x += maxWidth + edg.right;
            setBounds(new CGRect(0, 0, x, y));
            setContentSize(new CGSize(x, y));
        }

        @Override
        public CGSize sizeThatFits(CGSize size) {
            sizeToFit();
            return contentSize();
        }
    }

    protected static class MenuPopoverView extends UIPopoverView {

        private UIView contentView;

        @Override
        public void mouseDown(UIEvent event) {
            super.mouseDown(event);
            dismiss();
        }

        @Override
        public void keyDown(UIEvent event) {
            //
            if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
                event.cancel(InvokerResult.SUCCESS);
                dismiss();
                return;
            }
            super.keyDown(event);
        }

        @Override
        public UIView hitTest(CGPoint point, UIEvent event) {
            UIView hitView = super.hitTest(point, event);
            if (hitView != null) {
                return hitView;
            }
            return this;
        }

        @Override
        public UIView contentView() {
            return contentView;
        }

        @Override
        public void setContentView(UIView contentView) {
            if (this.contentView == contentView) {
                return;
            }
            if (this.contentView != null) {
                this.contentView.removeFromSuperview();
            }
            this.contentView = contentView;
            if (this.contentView != null) {
                this.addSubview(this.contentView);
                this.setNeedsLayout();
            }
        }
    }
}
