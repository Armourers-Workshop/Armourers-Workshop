package com.apple.library.uikit;

import com.apple.library.foundation.NSIndexPath;

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
}
