package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.WeakDispatcherImpl;
import com.apple.library.uikit.*;
import moe.plushie.armourers_workshop.init.ModTextures;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class TabView extends UIView {

    private Entry selectedTab;
    private boolean fullscreenMode = false;

    private final ArrayList<Entry> entries = new ArrayList<>();
    private final WeakDispatcherImpl<Entry> dispatcher = new WeakDispatcherImpl<>();
    private final UIView contentView = new UIView(bounds());

    public TabView(CGRect frame) {
        super(frame);
        this.addSubview(contentView);
    }

    public <T> void addTarget(T target, BiConsumer<T, Entry> listener) {
        dispatcher.add(target, listener);
    }

    public <T> void removeTarget(T target) {
        dispatcher.remove(target);
    }

    public EntryBuilder addContentView(UIView contentView) {
        Entry view = new Entry(contentView, new CGRect(0, 0, 26, 30));
        addSubview(view);
        entries.add(view);
        view.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, TabView::switchTab);
        return new EntryBuilder(view);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect bounds = this.bounds();
        this.contentView.setFrame(bounds);
        Entry entry = this.selectedTab;
        if (entry != null) {
            entry.contentView.setFrame(bounds);
        }
        if (this.fullscreenMode) {
            this.initFullscreenWidgets(0, 0, bounds.width, bounds.height);
        } else {
            this.initNormalWidgets(0, 0, bounds.width, bounds.height);
        }
    }

    @Override
    public boolean pointInside(CGPoint point, UIEvent event) {
        if (super.pointInside(point, event)) {
            return true;
        }
        return subviews().stream().anyMatch(subview -> subview.pointInside(convertPointToView(point, subview), event));
    }

    @Override
    public @Nullable UIView hitTest(CGPoint point, UIEvent event) {
        // we ignore tab view and content view the event response,
        // because in full screen mode, tab view or content view will intercept all events.
        UIView target = super.hitTest(point, event);
        if (target != this && target != this.contentView) {
            return target;
        }
        return null;
    }

    public Entry selectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(Entry selectedTab) {
        if (this.selectedTab != null) {
            this.selectedTab.contentView.removeFromSuperview();
            this.selectedTab.setSelected(false);
        }
        this.selectedTab = selectedTab;
        if (this.selectedTab != null) {
            this.selectedTab.setSelected(true);
            this.contentView.addSubview(this.selectedTab.contentView);
            this.setNeedsLayout();
            this.layoutIfNeeded();
        }
        this.dispatcher.send(selectedTab);
    }

    public Entry firstActiveTab() {
        for (Entry entry : entries) {
            if (!entry.isHidden()) {
                return entry;
            }
        }
        return null;
    }

    public Collection<Entry> tabs() {
        return entries;
    }

    public boolean fullscreenMode() {
        return fullscreenMode;
    }

    public void setFullscreenMode(boolean fullscreenMode) {
        this.fullscreenMode = fullscreenMode;
    }

    private void initNormalWidgets(int x, int y, int width, int height) {
        int ly = 5, ry = 5, spacing = -5;
        for (Entry tab : entries) {
            if (tab.isHidden()) {
                continue;
            }
            CGRect rect = tab.frame();
            if (tab.alignment == 0 && ly + rect.height <= height) { // left
                tab.setFrame(new CGRect(x + -rect.width + 5, y + ly, rect.width, rect.height));
                tab.updateAlignment(0);
                ly += rect.height;
                ly += spacing;
                continue;
            }
            if (ry + rect.height <= height) { // right
                tab.setFrame(new CGRect(x + width - 4, y + ry, rect.width, rect.height));
                tab.updateAlignment(1);
                ry += rect.height;
                ry += spacing;
            }
        }
    }

    private void initFullscreenWidgets(int x, int y, int width, int height) {
        int ly = 0, ry = 0, spacing = -2;
        for (Entry tab : entries) {
            if (tab.isHidden()) {
                continue;
            }
            CGRect rect = tab.frame();
            if (tab.alignment == 0 && ly + rect.height <= height) { // left
                tab.setFrame(new CGRect(x - 4, y + ly, rect.width, rect.height));
                tab.updateAlignment(1);
                ly += rect.height;
                ly += spacing;
                continue;
            }
            if (ry + rect.height <= height) { // right
                tab.setFrame(new CGRect(x + width - rect.width + 5, y + ry, rect.width, rect.height));
                tab.updateAlignment(0);
                ly += rect.height;
                ly += spacing;
            }
        }
        int mly = (height - (ly - spacing)) / 2, mry = (height - (ry - spacing)) / 2;
        for (Entry tab : entries) {
            int dy = mry;
            if (tab.alignment1 == 1) {
                dy = mly;
            }
            tab.setFrame(tab.frame().offset(0, dy));
        }
    }

    private void switchTab(UIControl control) {
        setSelectedTab((Entry) control);
    }

    public static class EntryBuilder {

        public final Entry view;
        public EntryBuilder(Entry view) {
            this.view = view;
        }

        public EntryBuilder setIcon(UIImage image) {
            this.view.iconView.setImage(image.snapshot());
            this.view.iconView.setHighlightedImage(image);
            return this;
        }

        public EntryBuilder setTooltip(Object tooltip) {
            this.view.iconView.setTooltip(tooltip);
            return this;
        }

        public EntryBuilder setAlignment(int alignment) {
            this.view.alignment = alignment;
            return this;
        }

        public EntryBuilder setTarget(Object target) {
            this.view.tag = target;
            return this;
        }

        public EntryBuilder setActive(boolean active) {
            this.view.setHidden(!active);
            return this;
        }
    }

    public static class Entry extends UIButton {

        protected int alignment = 0;
        protected int alignment1 = -1;

        protected CGRect validBounds;

        protected final UIImageView iconView;
        protected final UIView contentView;

        protected Object tag;

        public Entry(UIView contentView, CGRect frame) {
            super(frame);
            this.contentView = contentView;
            this.iconView = new UIImageView(new CGRect(0, 0, 16, 16));
            this.iconView.setUserInteractionEnabled(true);
            this.addSubview(iconView);
            this.setOpaque(false);
        }

        @Override
        public boolean pointInside(CGPoint point, UIEvent event) {
            return super.pointInside(point, event) && validBounds.contains(point);
        }

        @Override
        public void setBounds(CGRect bounds) {
            super.setBounds(bounds);
            this.validBounds = bounds.insetBy(3, 0, 3, 0);
        }

        @Nullable
        public Object tag() {
            return this.tag;
        }

        public UIView contentView() {
            return contentView;
        }

        protected void updateAlignment(int alignment) {
            if (this.alignment1 == alignment) {
                return;
            }
            CGRect rect = bounds();
            CGRect iconRect = iconView.bounds();
            int ix = (rect.width - iconRect.width) / 2 + (alignment - 1); // patch: 0: -1, 1: 0
            int iy = (rect.height - iconRect.height) / 2;
            this.alignment1 = alignment;
            this.setBackgroundImage(_tabButtonImages(alignment), State.ALL);
            this.iconView.setFrame(new CGRect(ix, iy, iconRect.width, iconRect.height));
        }

        private UIImage _tabButtonImages(int alignment) {
            // we use special mapping tables.
            HashMap<Integer, CGPoint> offsets = new HashMap<>();
            offsets.put(UIControl.State.NORMAL, new CGPoint(0, 1));
            offsets.put(UIControl.State.HIGHLIGHTED, new CGPoint(1, 1));
            offsets.put(UIControl.State.SELECTED | UIControl.State.NORMAL, new CGPoint(0, 0));
            offsets.put(UIControl.State.SELECTED | UIControl.State.HIGHLIGHTED, new CGPoint(1, 0));
            int width = 26;
            int height = 30;
            return UIImage.of(ModTextures.TABS).uv(width * alignment * 2, 0).size(width, height).unzip(offsets::get).build();
        }
    }
}
