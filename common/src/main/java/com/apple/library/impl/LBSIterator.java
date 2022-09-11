package com.apple.library.impl;

import java.util.Iterator;
import java.util.List;

public class LBSIterator<T> {

    boolean flag;

    public LBSIterator(boolean flag) {
        this.flag = flag;
    }

    public Iterable<T> remaining(List<T> views, T view) {
        int index = indexOf(views, view);
        if (flag) {
            return () -> new RangeIterator(views, index, views.size());
        } else {
            return () -> new ReverseRangeIterator(views, index, 0);
        }
    }

    public Iterable<T> skipping(List<T> views, T view) {
        int index = indexOf(views, view);
        if (flag) {
            return () -> new RangeIterator(views, 0, index);
        } else {
            return () -> new ReverseRangeIterator(views, views.size(), index);
        }
    }

    private int indexOf(List<T> views, T view) {
        if (view != null) {
            return views.indexOf(view);
        }
        return 0;
    }

    private class RangeIterator implements Iterator<T> {

        int offset;
        int size;
        List<T> items;

        RangeIterator(List<T> items, int offset, int size) {
            this.items = items;
            this.offset = offset;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return offset < size;
        }

        @Override
        public T next() {
            return items.get(offset++);
        }
    }

    private class ReverseRangeIterator implements Iterator<T> {

        int offset;
        int size;
        List<T> items;

        ReverseRangeIterator(List<T> items, int offset, int size) {
            this.items = items;
            this.offset = offset;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return offset > size;
        }

        @Override
        public T next() {
            return items.get(--offset);
        }
    }
}
