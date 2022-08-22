package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSIndexPath;
import com.apple.library.impl.HighlightedDisplayable;

public class UITableViewCell extends UIView implements HighlightedDisplayable {

    private NSIndexPath indexPath;

    private boolean isSelected = false;
    private boolean isHighlighted = false;

    public UITableViewCell(CGRect frame) {
        super(frame);
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        UITableView tableView = _tableView();
        if (tableView != null) {
            tableView.selectRow(indexPath, true);
        }
    }

    @Override
    public void mouseEntered(UIEvent event) {
        CGPoint point = convertPointFromView(event.locationInWindow(), null);
        boolean newHighlighted = pointInside(point, event);
        UITableView tableView = _tableView();
        if (tableView != null && isHighlighted != newHighlighted) {
            if (newHighlighted) {
                tableView._highlightRow(indexPath);
            } else {
                tableView._unhighlightRow(indexPath);
            }
        }
    }

    @Override
    public void mouseExited(UIEvent event) {
        UITableView tableView = _tableView();
        if (tableView != null && isHighlighted) {
            tableView._unhighlightRow(indexPath);
        }
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isHighlighted() {
        return this.isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
    }


    protected NSIndexPath _indexPath() {
        return indexPath;
    }

    protected void _setIndexPath(NSIndexPath indexPath) {
        this.indexPath = indexPath;
    }

    protected UITableView _tableView() {
        if (indexPath == null) {
            return null;
        }
        UIView view = superview();
        if (view instanceof UITableView) {
            return (UITableView) view;
        }
        return null;
    }
}
