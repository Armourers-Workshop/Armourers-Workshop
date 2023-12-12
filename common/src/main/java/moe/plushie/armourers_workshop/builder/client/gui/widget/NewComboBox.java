package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSIndexPath;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.impl.SoundManagerImpl;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UIPassthroughView;
import com.apple.library.uikit.UITableView;
import com.apple.library.uikit.UITableViewCell;
import com.apple.library.uikit.UITableViewDataSource;
import com.apple.library.uikit.UITableViewDelegate;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.init.ModTextures;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class NewComboBox extends UIControl implements UITableViewDataSource, UITableViewDelegate {

    protected final UIButton titleView = new UIButton(CGRect.ZERO);
    protected final UIView popoverView = new UIView(CGRect.ZERO);
    protected final UIImageView popoverBackgroundView = new UIImageView(CGRect.ZERO);
    protected final UITableView popoverContentView = new UITableView(CGRect.ZERO);
    protected final UIPassthroughView passthroughView = new UIPassthroughView(popoverView);

    protected final ArrayList<NewComboItem> sections = new ArrayList<>();

    protected NSIndexPath selectedIndex = new NSIndexPath(0, 0);
    protected int maxRows = 0;

    public NewComboBox(CGRect frame) {
        super(frame);
        this.setContents(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(24, 24).fixed(24, 20).clip(4, 4, 0, 4).build());
        this.popoverView.setContents(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(0, 0).resizable(24, 24).build());
        this.titleView.setUserInteractionEnabled(false);
        this.titleView.setHorizontalAlignment(NSTextAlignment.Horizontal.LEFT);
        this.titleView.setContentEdgeInsets(new UIEdgeInsets(0, 4, 0, 4));
        this.titleView.setTitleEdgeInsets(new UIEdgeInsets(0, 4, 0, 0));
        this.titleView.setTitleColor(UIColor.WHITE, State.NORMAL);
        this.titleView.setTitleColor(new UIColor(0xffffa0), State.HIGHLIGHTED);
        this.titleView.setTitleColor(new UIColor(0xcc0000), State.DISABLED);
        this.titleView.setCanBecomeFocused(false);
        this.titleView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.addSubview(titleView);
        this.popoverContentView.setRowHeight(16);
        this.popoverContentView.setDataSource(this);
        this.popoverContentView.setDelegate(this);
        this.popoverBackgroundView.setImage(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(96, 0).resizable(24, 24).build());
        this.popoverBackgroundView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        this.popoverView.addSubview(popoverBackgroundView);
        this.popoverView.setZIndex(200);
        this.popoverView.addSubview(popoverContentView);
        this.popoverView.setOpaque(false);
    }

    public void reloadData(List<? extends NewComboItem> items) {
        this.sections.clear();
        this.sections.addAll(items);
        this.applyTableViewSize();
        this.popoverContentView.reloadData();
        this.layoutIfNeeded();
        this.updateTitleView(selectedIndex);
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        SoundManagerImpl.click();
        setSelected(!isSelected());
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        float h = rect.height;
        titleView.setFrame(rect.insetBy(1, 1, 1, h + 1));
        applyTableViewSize();
    }

    @Override
    public void didMoveToWindow() {
        super.didMoveToWindow();
        this.updatePassthroughView();
    }

    @Override
    public int tableViewNumberOfSections(UITableView tableView) {
        return sections.size();
    }

    @Override
    public int tableViewNumberOfRowsInSection(UITableView tableView, int section) {
        return sections.get(section).size();
    }

    @Override
    public UITableViewCell tableViewCellForRow(UITableView tableView, NSIndexPath indexPath) {
        NewComboItem section = sections.get(indexPath.section);
        Cell cell = new Cell();
        updateEntryView(cell, section.get(indexPath.row), section);
        return cell;
    }

    @Override
    public UIView tableViewViewForHeaderInSection(UITableView tableView, int section) {
        NewComboItem sec = sections.get(section);
        UIButton view = new UIButton(CGRect.ZERO);
        view.setTitle(sec.getTitle(), State.NORMAL);
        view.setTitleColor(UIColor.WHITE, State.NORMAL);
        view.setHorizontalAlignment(NSTextAlignment.Horizontal.LEFT);
        view.setContentEdgeInsets(new UIEdgeInsets(0, 4, 0, 4));
        return view;
    }

    @Override
    public float tableViewHeightForHeaderInSection(UITableView tableView, int section) {
        return tableView.rowHeight() + 4;
    }

    @Override
    public void tableViewDidSelectRow(UITableView tableView, NSIndexPath indexPath) {
        setSelectedIndex(indexPath);
        sendEvent(Event.VALUE_CHANGED);
    }

    @Override
    public void setSelected(boolean isSelected) {
        super.setSelected(isSelected);
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
        updatePassthroughView();
    }

    public NSIndexPath selectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(NSIndexPath selectedIndex) {
        this.selectedIndex = selectedIndex;
        this.updateTitleView(selectedIndex);
    }

    public int maxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    protected void updateTitleView(NSIndexPath indexPath) {
        // ignore session missing.
        if (indexPath.section < 0 || indexPath.section >= sections.size()) {
            return;
        }
        NewComboItem section = sections.get(indexPath.section);
        // ignore item missing.
        if (indexPath.row < 0 || indexPath.row >= section.size()) {
            return;
        }
        UIComboItem item = section.get(indexPath.row);
        NSMutableString name = new NSMutableString(section.getTitle());
        name.append(" - ");
        name.append(item.title);
        titleView.setTitle(name, State.ALL);
        titleView.setImage(item.image, State.ALL);
    }

    protected void updateEntryView(Cell entry, UIComboItem item, NewComboItem section) {
        entry.setup(item);
    }

    @Override
    protected boolean shouldBeHighlight(CGPoint point, UIEvent event) {
        return bounds().contains(point);
    }

    @Override
    protected boolean shouldPassHighlighted() {
        return false;
    }

    private void applyTableViewSize() {
        CGRect rect = bounds();
        float popoverHeight = 0;
        if (maxRows != 0) {
            popoverHeight = maxRows * popoverContentView.rowHeight();
        }
        if (popoverHeight == 0) {
            UIEdgeInsets edg = popoverContentView.contentInsets();
            popoverHeight = edg.top + popoverContentView.contentSize().height + edg.bottom;
        }
        popoverView.setFrame(new CGRect(0, rect.height, rect.width, popoverHeight));
        popoverBackgroundView.setFrame(popoverView.bounds());
        popoverContentView.setFrame(popoverView.bounds());
    }

    private void addGlobalClickListener() {
        UIWindow window = window();
        if (window == null) {
            return;
        }
        window.addGlobalTarget(this, Event.MOUSE_LEFT_DOWN, (self, event) -> {
            if (self.isOutsideEvent(event)) {
                self.setSelected(false);
                self.removeGlobalClickListener();
            }
        });
    }

    private void removeGlobalClickListener() {
        UIWindow window = window();
        if (window == null) {
            return;
        }
        window.removeGlobalTarget(this, Event.MOUSE_LEFT_DOWN);
    }

    private void updatePassthroughView() {
        UIWindow window = window();
        if (window != null && popoverView.superview() != null) {
            if (passthroughView.superview() != window) {
                window.addSubview(passthroughView);
            }
        } else {
            if (passthroughView.superview() != null) {
                passthroughView.removeFromSuperview();
            }
        }
    }

    private boolean isOutsideEvent(UIEvent event) {
        if (pointInside(event.locationInView(this), event)) {
            return false;
        }
        return !popoverView.pointInside(event.locationInView(popoverView), event);
    }

    public static class Cell extends UITableViewCell {

        private static final UIColor BACKGROUND_COLOR = new UIColor(0x44cccccc, true);

        public final UIButton titleView = new UIButton(CGRect.ZERO);

        public Cell() {
            super(CGRect.ZERO);
            this.titleView.setUserInteractionEnabled(false);
            this.titleView.setHorizontalAlignment(NSTextAlignment.Horizontal.LEFT);
            this.titleView.setContentEdgeInsets(new UIEdgeInsets(0, 8, 0, 4));
            this.titleView.setTitleEdgeInsets(new UIEdgeInsets(0, 4, 0, 0));
            this.titleView.setTitleColor(UIColor.WHITE, State.NORMAL);
            this.titleView.setTitleColor(new UIColor(0xffffa0), State.HIGHLIGHTED);
            this.titleView.setTitleColor(new UIColor(0xcc0000), State.DISABLED);
            this.titleView.setCanBecomeFocused(false);
            this.titleView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
            this.addSubview(titleView);
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

