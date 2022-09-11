package com.apple.library.impl;

import java.util.Iterator;
import java.util.ListIterator;

public class ReversedIteratorImpl<T> implements Iterator<T> {

    public ListIterator<T> iterator;

    public ReversedIteratorImpl(ListIterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasPrevious();
    }

    @Override
    public T next() {
        return iterator.previous();
    }
}
