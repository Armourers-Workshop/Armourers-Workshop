package com.apple.library.uikit;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSIndexPath;
import com.apple.library.impl.DelegateImpl;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class UITableView extends UIScrollView {

    private int rowHeight = 24;
    private boolean allowsMultipleSection = false;

    protected final DelegateImpl<UITableViewDelegate> delegate = DelegateImpl.of(new UITableViewDelegate() {
    });
    protected final DelegateImpl<UITableViewDataSource> dataSource = DelegateImpl.of(new UITableViewDataSource() {

        @Override
        public int tableViewNumberOfRowsInSection(UITableView tableView, int section) {
            return 0;
        }

        @Override
        public UITableViewCell tableViewCellForRow(UITableView tableView, NSIndexPath indexPath) {
            return null;
        }
    });

    private float cachedWidth = 0;
    private final HashSet<NSIndexPath> selectedIndexPaths = new HashSet<>();

    private ArrayList<UIView> entries;
    private HashMap<NSIndexPath, UIView> indexedEntries;

    public UITableView(CGRect frame) {
        super(frame);
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        float width = bounds().getWidth();
        if (width != cachedWidth) {
            cachedWidth = width;
            reloadData();
        }
    }

    public void reloadData() {
        var bounds = bounds();
        var entries = new ArrayList<UIView>();
        var indexedEntries = new HashMap<NSIndexPath, UIView>();
        var delegate = this.delegate.invoker();
        var dataSource = this.dataSource.invoker();
        var height = 0f;
        var sections = dataSource.tableViewNumberOfSections(this);
        for (var section = 0; section < sections; ++section) {
            var rows = dataSource.tableViewNumberOfRowsInSection(this, section);
            var headerHeight = delegate.tableViewHeightForHeaderInSection(this, section);
            if (headerHeight != 0) {
                UIView headerView = delegate.tableViewViewForHeaderInSection(this, section);
                if (headerView != null) {
                    headerView.setFrame(new CGRect(0, height, bounds.width, headerHeight));
                    entries.add(headerView);
                    height += headerHeight;
                }
            }
            for (var row = 0; row < rows; ++row) {
                var indexPath = new NSIndexPath(row, section);
                var cell = dataSource.tableViewCellForRow(this, indexPath);
                var rowHeight = heightForRow(indexPath);
                cell._setIndexPath(indexPath);
                cell.setFrame(new CGRect(0, height, bounds.width, rowHeight));
                entries.add(cell);
                indexedEntries.put(indexPath, cell);
                height += rowHeight;
            }
            var footerHeight = delegate.tableViewHeightForFooterInSection(this, section);
            if (footerHeight != 0) {
                var footerView = delegate.tableViewViewForFooterInSection(this, section);
                if (footerView != null) {
                    footerView.setFrame(new CGRect(0, height, bounds.width, footerHeight));
                    entries.add(footerView);
                    height += footerHeight;
                }
            }
        }
        if (this.entries != null) {
            this.entries.forEach(UIView::removeFromSuperview);
        }
        this.indexedEntries = indexedEntries;
        this.entries = entries;
        this.entries.forEach(this::addSubview);
        this.setContentSize(new CGSize(0, height));
    }

    public int rowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public UITableViewDataSource dataSource() {
        return this.dataSource.get();
    }

    public void setDataSource(UITableViewDataSource dataSource) {
        this.dataSource.set(dataSource);
    }

    @Override
    public UITableViewDelegate delegate() {
        return this.delegate.get();
    }

    public void setDelegate(UITableViewDelegate delegate) {
        super.setDelegate(delegate);
        this.delegate.set(delegate);
    }

    @Nullable
    public NSIndexPath indexPathForSelectedRow() {
        return selectedIndexPaths.stream().findFirst().orElse(null);
    }

    @Nullable
    public Collection<NSIndexPath> indexPathsForSelectedRows() {
        return selectedIndexPaths;
    }

    public NSIndexPath indexPathForCell(UITableViewCell cell) {
        return cell._indexPath();
    }

    public UITableViewCell cellForRow(NSIndexPath indexPath) {
        var entry = indexedEntries.get(indexPath);
        if (entry instanceof UITableViewCell cell) {
            return cell;
        }
        return null;
    }

    public void selectRow(NSIndexPath indexPath, boolean animated) {
        indexPath = delegate.invoker().tableViewWillSelectRow(this, indexPath);
        if (indexPath == null) {
            return; // user reject.
        }
        if (selectedIndexPaths.contains(indexPath)) {
            if (!allowsMultipleSection) {
                deselectRow(indexPath, animated);
            }
            return;
        }
        if (!allowsMultipleSection) {
            for (var indexPath1 : selectedIndexPaths) {
                deselectRow(indexPath1, animated);
            }
        }
        selectedIndexPaths.add(indexPath);
        apply(UITableViewCell::setSelected, indexPath, true);
        delegate.invoker().tableViewDidSelectRow(this, indexPath);
    }

    public void deselectRow(NSIndexPath indexPath, boolean animated) {
        indexPath = delegate.invoker().tableViewWillDeselectRow(this, indexPath);
        if (indexPath == null) {
            return; // user reject
        }
        selectedIndexPaths.remove(indexPath);
        apply(UITableViewCell::setSelected, indexPath, false);
        delegate.invoker().tableViewDidDeselectRow(this, indexPath);
    }

    protected void _highlightRow(NSIndexPath indexPath) {
        if (!delegate.invoker().tableViewShouldHighlightRow(this, indexPath)) {
            return;
        }
        apply(UITableViewCell::setHighlighted, indexPath, true);
        delegate.invoker().tableViewDidHighlightRow(this, indexPath);
    }

    protected void _unhighlightRow(NSIndexPath indexPath) {
        apply(UITableViewCell::setHighlighted, indexPath, false);
        delegate.invoker().tableViewDidUnhighlightRow(this, indexPath);
    }

    private float heightForRow(NSIndexPath indexPath) {
        var height = delegate.invoker().tableViewHeightForRowAt(this, indexPath);
        if (height != 0) {
            return height;
        }
        return rowHeight;
    }

    private <T> void apply(BiConsumer<UITableViewCell, T> func, NSIndexPath indexPath, T value) {
        var cell = cellForRow(indexPath);
        if (cell != null) {
            func.accept(cell, value);
        }
    }
}
