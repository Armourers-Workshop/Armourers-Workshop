package moe.plushie.armourers_workshop.core.math;

import moe.plushie.armourers_workshop.api.core.math.IModelViewStack;

import java.util.Stack;

public class OpenModelViewStack implements IModelViewStack {

    protected OpenMatrix4f entry = new OpenMatrix4f();
    protected Stack<OpenMatrix4f> stack;

    @Override
    public void pushMatrix() {
        if (stack == null) {
            stack = new Stack<>();
        }
        stack.push(entry);
        entry = new OpenMatrix4f(entry);
    }

    @Override
    public void popMatrix() {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        entry = stack.pop();
    }

    @Override
    public OpenMatrix4f last() {
        return entry;
    }
}
