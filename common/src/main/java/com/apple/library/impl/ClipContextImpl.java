package com.apple.library.impl;

import com.apple.library.coregraphics.CGRect;

import java.util.Stack;

public class ClipContextImpl {

    private static final CGRect EMPTY_BOX = new CGRect(-4000000, -4000000, 8000000, 8000000);

    private final Stack<CGRect> stack = new Stack<>();

    public ClipContextImpl() {
        stack.push(EMPTY_BOX);
    }

    public void push(CGRect rect) {
        stack.push(stack.peek().intersection(rect));
    }

    public void pop() {
        stack.pop();
    }

    public CGRect peek() {
        return stack.peek();
    }
}
