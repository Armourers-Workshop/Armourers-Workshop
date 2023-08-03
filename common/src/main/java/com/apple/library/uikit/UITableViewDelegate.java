package com.apple.library.uikit;

import com.apple.library.foundation.NSIndexPath;

@SuppressWarnings("unused")
public interface UITableViewDelegate extends UIScrollViewDelegate {

    default boolean tableViewShouldHighlightRow(UITableView tableView, NSIndexPath indexPath) {
        return true;
    }

    default void tableViewDidHighlightRow(UITableView tableView, NSIndexPath indexPath) {
    }

    default void tableViewDidUnhighlightRow(UITableView tableView, NSIndexPath indexPath) {
    }

    default NSIndexPath tableViewWillSelectRow(UITableView tableView, NSIndexPath indexPath) {
        return indexPath;
    }

    default NSIndexPath tableViewWillDeselectRow(UITableView tableView, NSIndexPath indexPath) {
        return indexPath;
    }

    default void tableViewDidSelectRow(UITableView tableView, NSIndexPath indexPath) {
    }

    default void tableViewDidDeselectRow(UITableView tableView, NSIndexPath indexPath) {
    }

    default float tableViewHeightForRowAt(UITableView tableView, NSIndexPath indexPath) {
        return 0;
    }

    default float tableViewHeightForHeaderInSection(UITableView tableView, int section) {
        return 0;
    }

    default float tableViewHeightForFooterInSection(UITableView tableView, int section) {
        return 0;
    }

    default UIView tableViewViewForHeaderInSection(UITableView tableView, int section) {
        return null;
    }

    default UIView tableViewViewForFooterInSection(UITableView tableView, int section) {
        return null;
    }
}
