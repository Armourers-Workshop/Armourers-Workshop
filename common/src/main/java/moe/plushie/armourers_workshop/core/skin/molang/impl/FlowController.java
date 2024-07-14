package moe.plushie.armourers_workshop.core.skin.molang.impl;

import moe.plushie.armourers_workshop.core.skin.molang.core.Compound;
import moe.plushie.armourers_workshop.core.skin.molang.core.Constant;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Function;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Stack;

public class FlowController {

    private FlowController parent;

    private State state = State.NONE;
    private Expression result = Constant.ZERO;

    private final Kind kind;

    public FlowController(Kind kind) {
        this.kind = kind;
    }

    public static FlowController block(List<Expression> expressions) {
        var control = new FlowController(Kind.BLOCK);
        var applier = new Applier(control);
        expressions.forEach(it -> it.visit(applier));
        return control;
    }

    public static FlowController enumerate(Expression expression) {
        var control = new FlowController(Kind.ENUMERATE);
        var applier = new Applier(control);
        expression.visit(applier);
        return control;
    }

    public static FlowController instruct() {
        return new FlowController(Kind.INSTRUCT);
    }

    public void begin() {
        state = State.NONE;
        result = Constant.ZERO;
    }

    public Expression end() {
        return result;
    }

    public State interrupt() {
        return state;
    }

    public void setInterrupt(State state) {
        setInterrupt(state, Constant.ZERO);
    }

    public void setInterrupt(State state, Expression result) {
        var target = findTarget(state);
        var controller = parent;
        while (controller != null) {
            controller.state = state;
            if (controller.kind == target) {
                controller.result = result;
                break;
            }
            controller = controller.parent;
        }
    }

    public FlowController parent() {
        return parent;
    }

    public void setParent(FlowController parent) {
        this.parent = parent;
    }

    @Nullable
    private Kind findTarget(State state) {
        return switch (state) {
            case RETURN -> Kind.BLOCK;
            case BREAK, CONTINUE -> Kind.ENUMERATE;
            case NONE -> null;
        };
    }

    public enum Kind {
        BLOCK,
        ENUMERATE,
        INSTRUCT,
    }

    public enum State {
        NONE,
        BREAK,
        CONTINUE,
        RETURN;

        public boolean isContinueOrBreakOrReturn() {
            return this != NONE;
        }

        public boolean isBreakOrReturn() {
            return this == BREAK || this == RETURN;
        }
    }

    public static class Applier extends TreeVisitor {

        private final Stack<FlowController> stack = new Stack<>();

        public Applier(FlowController control) {
            stack.push(control);
        }

        @Override
        public Expression visit(Expression expression) {
            if (expression instanceof FlowControllable controllable) {
                // never self-assign.
                if (controllable.controller() != stack.peek()) {
                    controllable.controller().setParent(stack.peek());
                }
            }
            return expression;
        }

        @Override
        public Expression visitFunction(Function expression) {
            if (expression instanceof FlowControllable controllable && controllable.controller().parent() != stack.peek()) {
                controllable.controller().setParent(stack.peek());
                stack.push(controllable.controller());
                super.visitFunction(expression);
                stack.pop();
            }
            return expression;
        }

        @Override
        public Expression visitCompound(Compound expression) {
            if (expression.controller().parent() != stack.peek()) {
                expression.controller().setParent(stack.peek());
                stack.push(expression.controller());
                super.visitCompound(expression);
                stack.pop();
            }
            return expression;
        }
    }
}
