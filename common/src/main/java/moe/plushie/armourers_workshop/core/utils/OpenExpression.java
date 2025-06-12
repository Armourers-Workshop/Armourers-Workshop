package moe.plushie.armourers_workshop.core.utils;

public class OpenExpression {

    private final String expression;

    public OpenExpression(String expression) {
        this.expression = expression;
    }

    public String expression() {
        return this.expression;
    }

    @Override
    public String toString() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenExpression that)) return false;
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return expression.hashCode();
    }
}

