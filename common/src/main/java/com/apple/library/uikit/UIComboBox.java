package com.apple.library.uikit;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSIndexPath;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.impl.AppearanceImpl;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UIComboBox extends UIControl implements UITableViewDataSource, UITableViewDelegate {

    protected final Entry titleView = new Entry();
    protected final UIButton handleView = new UIButton(CGRect.ZERO);

    protected final UIView popoverView = new UIView(CGRect.ZERO);
    protected final UITableView popoverContentView = new UITableView(CGRect.ZERO);

    protected final ArrayList<UIComboItem> items = new ArrayList<>();

    protected int selectedIndex = 0;
    protected int maxRows = 0;

    private UIImage backgroundImage;

    public UIComboBox(CGRect frame) {
        super(frame);
        this.setBackgroundImage(AppearanceImpl.BUTTON_IMAGE.imageAtIndex(UIControl.State.DISABLED));
        this.handleView.setBackgroundImage(AppearanceImpl.BUTTON_IMAGE, State.ALL);
        this.handleView.setUserInteractionEnabled(false);
        this.handleView.setTitle(new NSString("⋁"), State.NORMAL);
        this.handleView.setTitle(new NSString("⋀"), State.SELECTED);
        this.handleView.setTitleColor(UIColor.WHITE, State.ALL);
        this.handleView.setCanBecomeFocused(false);
        this.titleView.titleView.setEnabled(false);
        this.titleView.setClipBounds(true);
        this.titleView.setUserInteractionEnabled(false);
        this.addSubview(handleView);
        this.addSubview(titleView);
        this.popoverContentView.setRowHeight(13);
        this.popoverContentView.setDataSource(this);
        this.popoverContentView.setDelegate(this);
        this.popoverView.setZIndex(200);
        this.popoverView.addSubview(popoverContentView);
    }

    public void reloadData(List<UIComboItem> items) {
        this.items.clear();
        this.items.addAll(items);
        this.applyTableViewSize();
        this.popoverContentView.reloadData();
        this.layoutIfNeeded();
        this.updateTitleView(safeGet(selectedIndex));
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        setSelected(!isSelected());
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        int h = rect.height;
        titleView.setFrame(rect.insetBy(1, 1, 1, h + 1));
        handleView.setFrame(new CGRect(rect.width - h, 0, h, h));
        applyTableViewSize();
    }

    @Override
    public boolean pointInside(CGPoint point, UIEvent event) {
        if (super.pointInside(point, event)) {
            return true;
        }
        if (popoverView.superview() != null) {
            return popoverView.pointInside(convertPointToView(point, popoverView), event);
        }
        return false;
    }

    @Override
    public int tableViewNumberOfRowsInSection(UITableView tableView, int section) {
        return items.size();
    }

    @Override
    public UITableViewCell tableViewCellForRow(UITableView tableView, NSIndexPath indexPath) {
        Entry entry = new Entry();
        updateEntryView(entry, items.get(indexPath.row));
        return entry;
    }

    @Override
    public void tableViewDidSelectRow(UITableView tableView, NSIndexPath indexPath) {
        setSelectedIndex(indexPath.row);
        sendEvent(Event.VALUE_CHANGED);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        context.drawImage(backgroundImage, bounds());
        super.render(point, context);
    }

    public UIImage backgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(UIImage backgroundImage) {
        this.backgroundImage = backgroundImage;
        this.popoverView.setContents(backgroundImage);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        super.setHighlighted(highlighted);
        handleView.setHighlighted(highlighted);
    }

    @Override
    public void setSelected(boolean isSelected) {
        super.setSelected(isSelected);
        if (isSelected == handleView.isSelected()) {
            return;
        }
        handleView.setSelected(isSelected);
        popoverView.removeFromSuperview();
        if (isSelected) {
            popoverContentView.setContentOffset(CGPoint.ZERO);
            addSubview(popoverView);
        }
        if (isSelected) {
            addGlobalClickListener();
        } else {
            removeGlobalClickListener();
        }
    }

    public int selectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        this.updateTitleView(safeGet(selectedIndex));
    }

    public int maxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    protected void updateTitleView(UIComboItem comboItem) {
        updateEntryView(titleView, comboItem);
    }

    protected void updateEntryView(Entry entry, UIComboItem comboItem) {
        entry.setup(comboItem);
    }

    @Override
    protected boolean shouldBeHighlight(CGPoint point, UIEvent event) {
        return bounds().contains(point);
    }

    @Override
    protected boolean shouldPassHighlighted() {
        return false;
    }

    private UIComboItem safeGet(int row) {
        if (row >= 0 && row < items.size()) {
            return items.get(row);
        }
        return null;
    }

    private void applyTableViewSize() {
        CGRect rect = bounds();
        int popoverHeight = 0;
        if (maxRows != 0) {
            popoverHeight = maxRows * popoverContentView.rowHeight();
        }
        if (popoverHeight == 0) {
            UIEdgeInsets edg = popoverContentView.contentInsets();
            popoverHeight = edg.top + popoverContentView.contentSize().height + edg.bottom;
        }
        popoverView.setFrame(new CGRect(0, rect.height + 1, rect.width, popoverHeight + 2));
        popoverContentView.setFrame(popoverView.bounds().insetBy(1, 1, 1, 1));
    }

    private void addGlobalClickListener() {
        UIWindow window = window();
        if (window != null) {
            window.addGlobalTarget(this, Event.MOUSE_LEFT_DOWN, (self, event) -> {
                self.removeGlobalClickListener();
                CGPoint point = self.convertPointFromView(event.locationInWindow(), null);
                if (!self.pointInside(point, event)) {
                    self.setSelected(false);
                }
            });
        }
    }

    private void removeGlobalClickListener() {
        UIWindow window = window();
        if (window != null) {
            window.removeGlobalTarget(this, Event.MOUSE_LEFT_DOWN);
        }
    }

    public static class Entry extends UITableViewCell {

        private static final UIColor BACKGROUND_COLOR = new UIColor(0x44cccccc, true);

        public final UIButton titleView = new UIButton(CGRect.ZERO);

        public Entry() {
            super(CGRect.ZERO);
            this.titleView.titleView().setShadowColor(null);
            this.titleView.setUserInteractionEnabled(false);
            this.titleView.setHorizontalAlignment(NSTextAlignment.Horizontal.LEFT);
            this.titleView.setContentEdgeInsets(new UIEdgeInsets(0, 2, 0, 2));
            this.titleView.setTitleColor(UIColor.WHITE, State.NORMAL);
            this.titleView.setTitleColor(new UIColor(0xffffa0), State.HIGHLIGHTED);
            this.titleView.setTitleColor(new UIColor(0xcc0000), State.DISABLED);
            this.titleView.setCanBecomeFocused(false);
            this.addSubview(titleView);
        }

        @Override
        public void layoutSubviews() {
            super.layoutSubviews();
            titleView.setFrame(bounds());
        }

        public void setup(@Nullable UIComboItem item) {
            if (item == null) {
                this.setHidden(true);
                return;
            }
            this.titleView.setTitle(item.title, State.ALL);
            this.titleView.setImage(item.image, State.ALL);
            this.titleView.setEnabled(item.isEnabled);
            this.setHidden(false);
        }

        @Override
        public void setHighlighted(boolean highlighted) {
            super.setHighlighted(highlighted);
            titleView.setHighlighted(highlighted);
            if (highlighted) {
                setBackgroundColor(BACKGROUND_COLOR);
            } else {
                setBackgroundColor(null);
            }
        }
    }
}
