package com.apple.library.uikit;

import com.apple.library.foundation.NSIndexPath;

@SuppressWarnings("unused")
public interface UITableViewDataSource {

    default int tableViewNumberOfSections(UITableView tableView) {
        return 1;
    }

    int tableViewNumberOfRowsInSection(UITableView tableView, int section);

    UITableViewCell tableViewCellForRow(UITableView tableView, NSIndexPath indexPath);
}
