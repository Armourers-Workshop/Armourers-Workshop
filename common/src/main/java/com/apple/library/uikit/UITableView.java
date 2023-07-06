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

public class UITableView extends UIScrollView {

    private int rowHeight = 24;
    private boolean allowsMultipleSection = false;

    protected final DelegateImpl<UITableViewDelegate> delegate = DelegateImpl.of(new UITableViewDelegate() {});
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

    private ArrayList<Entry> entries;
    private HashMap<NSIndexPath, Entry> indexedEntries;

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
        CGRect bounds = bounds();
        ArrayList<Entry> entries = new ArrayList<>();
        HashMap<NSIndexPath, Entry> indexedEntries = new HashMap<>();
        UITableViewDataSource delegate = this.dataSource();
        int height = 0;
        int sections = delegate.tableViewNumberOfSections(this);
        for (int section = 0; section < sections; ++section) {
            int rows = delegate.tableViewNumberOfRowsInSection(this, section);
            for (int row = 0; row < rows; ++row) {
                NSIndexPath indexPath = new NSIndexPath(row, section);
                UITableViewCell cell = delegate.tableViewCellForRow(this, indexPath);
                Entry entry = new Entry(cell, indexPath);
                cell.setFrame(new CGRect(0, height, bounds.width, rowHeight));
                entries.add(entry);
                indexedEntries.put(indexPath, entry);
                height += rowHeight;
            }
        }
        if (this.entries != null) {
            this.entries.forEach(Entry::remove);
        }
        this.indexedEntries = indexedEntries;
        this.entries = entries;
        this.entries.forEach(Entry::add);
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
        Entry entry = entryForRow(indexPath);
        if (entry != null) {
            return entry.cell;
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
            for (NSIndexPath indexPath1 : selectedIndexPaths) {
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

    private Entry entryForRow(NSIndexPath indexPath) {
        if (indexedEntries != null) {
            return indexedEntries.get(indexPath);
        }
        return null;
    }

    private <T> void apply(BiConsumer<UITableViewCell, T> func, NSIndexPath indexPath, T value) {
        UITableViewCell cell = cellForRow(indexPath);
        if (cell != null) {
            func.accept(cell, value);
        }
    }

    private class Entry {

        NSIndexPath indexPath;
        UITableViewCell cell;

        Entry(UITableViewCell cell, NSIndexPath indexPath) {
            this.indexPath = indexPath;
            this.cell = cell;
            this.cell._setIndexPath(indexPath);
        }

        void add() {
            addSubview(cell);
        }

        void remove() {
            cell.removeFromSuperview();
        }
    }
}
