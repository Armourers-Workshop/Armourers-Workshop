package com.apple.library.foundation;

import java.util.Objects;

public class NSIndexPath {

    public final int row;
    public final int section;

    public NSIndexPath(int row, int section) {
        this.row = row;
        this.section = section;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NSIndexPath)) return false;
        NSIndexPath that = (NSIndexPath) o;
        return row == that.row && section == that.section;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, section);
    }
}
