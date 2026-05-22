package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class Result {

    public static final Result NULL = new NullValue();

    public static final Result ONE = new NumberValue(1);
    public static final Result ZERO = new NumberValue(0);

    public static Result newArray() {
        return new ArrayValue(new ArrayList<>());
    }

    public static Result newStruct() {
        return new StructValue(new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public static Result parse(Object value) {
        if (value == null) {
            return Result.NULL;
        }
        if (value instanceof Result result) {
            return result;
        }
        if (value instanceof Boolean boolValue) {
            return valueOf(boolValue);
        }
        if (value instanceof Double doubleValue) {
            return valueOf(doubleValue);
        }
        if (value instanceof String stringValue) {
            return valueOf(stringValue);
        }
        if (value instanceof Number numberValue) {
            return valueOf(numberValue.doubleValue());
        }
        if (value instanceof List<?> arrayValue) {
            return valueOf((List<Result>) arrayValue);
        }
        if (value instanceof Map<?, ?> structValue) {
            return valueOf((Map<Name, Result>) structValue);
        }
        return wrap(value);
    }

    public static Result valueOf(boolean value) {
        if (value) {
            return ONE;
        }
        return ZERO;
    }

    public static Result valueOf(double value) {
        return new NumberValue(value);
    }

    public static Result valueOf(String value) {
        return new StringValue(value);
    }

    public static Result valueOf(List<Result> value) {
        return new ArrayValue(value);
    }

    public static Result valueOf(Map<Name, Result> value) {
        return new StructValue(value);
    }

    public static Result wrap(Object reference) {
        return new ReferenceValue(reference);
    }

    public boolean notEquals(Object o) {
        return !equals(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result that)) return false;
        if (type() != that.type()) return false;
        return switch (type()) {
            case NULL -> true;
            case NUMBER -> Double.compare(doubleValue(), that.doubleValue()) == 0;
            case STRING -> Objects.equals(stringValue(), that.stringValue());
            case ARRAY -> Objects.equals(arrayValue(), that.arrayValue());
            case STRUCT -> Objects.equals(structValue(), that.structValue());
            case REFERENCE -> Objects.equals(referenceValue(), that.referenceValue());
        };
    }

    @Override
    public int hashCode() {
        var hash = switch (type()) {
            case NULL -> 0;
            case NUMBER -> Objects.hash(doubleValue());
            case STRING -> Objects.hash(stringValue());
            case ARRAY -> Objects.hash(arrayValue());
            case STRUCT -> Objects.hash(structValue());
            case REFERENCE -> Objects.hash(referenceValue());
        };
        hash = 31 * hash + type().hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return switch (type()) {
            case NULL -> "<null>";
            case NUMBER -> String.format("%s", doubleValue());
            case STRING -> String.format("%s", stringValue());
            case ARRAY -> String.format("%s", arrayValue());
            case STRUCT -> String.format("%s", structValue());
            case REFERENCE -> String.format("%s", referenceValue());
        };
    }

    public void set(int index, Result value) {
        var arrayValue = arrayValue();
        if (arrayValue != null && !arrayValue.isEmpty()) {
            arrayValue.set(Math.max(index, 0) % arrayValue.size(), value);
        }
    }

    public Result get(int index) {
        var arrayValue = arrayValue();
        if (arrayValue != null && !arrayValue.isEmpty()) {
            return arrayValue.get(Math.max(index, 0) % arrayValue.size());
        }
        return Result.NULL;
    }

    public void set(Name name, Result value) {
        var structValue = structValue();
        if (structValue != null) {
            structValue.put(name, value);
        }
    }

    public Result get(Name name) {
        var structValue = structValue();
        if (structValue != null) {
            return structValue.getOrDefault(name, Result.NULL);
        }
        return Result.NULL;
    }

    public int size() {
        var arrayValue = arrayValue();
        if (arrayValue != null) {
            return arrayValue.size();
        }
        var structValue = structValue();
        if (structValue != null) {
            return structValue.size();
        }
        return 0;
    }

    public abstract Result copy();

    public boolean booleanValue() {
        return doubleValue() != 0.0;
    }

    public int intValue() {
        return MathHelper.floor(doubleValue());
    }

    public double doubleValue() {
        return 0;
    }

    public String stringValue() {
        return toString();
    }

    public List<Result> arrayValue() {
        return null;
    }

    public Map<Name, Result> structValue() {
        return null;
    }

    public Object referenceValue() {
        return null;
    }

    public boolean isValid() {
        return type() != Type.NULL;
    }

    public boolean isNull() {
        return type() == Type.NULL;
    }

    public boolean isNumber() {
        return type() == Type.NUMBER;
    }

    public boolean isString() {
        return type() == Type.STRING;
    }

    public boolean isArray() {
        return type() == Type.ARRAY;
    }

    public boolean isStruct() {
        return type() == Type.STRUCT;
    }

    public abstract Type type();

    public enum Type {
        NULL, NUMBER, STRING, ARRAY, STRUCT, REFERENCE
    }

    private static class NullValue extends Result {

        @Override
        public Result copy() {
            return this;
        }

        @Override
        public Type type() {
            return Type.NULL;
        }
    }

    private static class NumberValue extends Result {

        private final double wrappedValue;

        private NumberValue(double wrappedValue) {
            this.wrappedValue = wrappedValue;
        }

        @Override
        public Result copy() {
            return new NumberValue(wrappedValue);
        }

        @Override
        public double doubleValue() {
            return wrappedValue;
        }

        @Override
        public Type type() {
            return Type.NUMBER;
        }
    }

    private static class StringValue extends Result {

        private final String wrappedValue;

        private StringValue(String wrappedValue) {
            this.wrappedValue = wrappedValue;
        }

        @Override
        public Result copy() {
            return new StringValue(wrappedValue);
        }

        @Override
        public String stringValue() {
            if (wrappedValue != null) {
                return wrappedValue;
            }
            return super.stringValue();
        }

        @Override
        public Type type() {
            return Type.STRING;
        }
    }

    private static class ArrayValue extends Result {

        private final List<Result> wrappedValue;

        private ArrayValue(List<Result> wrappedValue) {
            this.wrappedValue = wrappedValue;
        }

        @Override
        public Result copy() {
            if (wrappedValue == null) {
                return new ArrayValue(null);
            }
            var newValue = new ArrayList<Result>(wrappedValue.size());
            for (var element : wrappedValue) {
                newValue.add(element.copy());
            }
            return new ArrayValue(newValue);
        }

        @Override
        public List<Result> arrayValue() {
            return wrappedValue;
        }

        @Override
        public Type type() {
            return Type.ARRAY;
        }
    }

    private static class StructValue extends Result {

        private final Map<Name, Result> wrappedValue;

        private StructValue(Map<Name, Result> wrappedValue) {
            this.wrappedValue = wrappedValue;
        }

        @Override
        public Result copy() {
            if (wrappedValue == null) {
                return new StructValue(null);
            }
            var newValue = new HashMap<Name, Result>(wrappedValue.size());
            for (var element : wrappedValue.entrySet()) {
                newValue.put(element.getKey(), element.getValue().copy());
            }
            return new StructValue(newValue);
        }

        @Override
        public Map<Name, Result> structValue() {
            return wrappedValue;
        }

        @Override
        public Type type() {
            return Type.STRUCT;
        }
    }

    private static class ReferenceValue extends Result {

        private final Object wrappedValue;

        private ReferenceValue(Object wrappedValue) {
            this.wrappedValue = wrappedValue;
        }

        @Override
        public Result copy() {
            return new ReferenceValue(wrappedValue);
        }

        @Override
        public Object referenceValue() {
            return wrappedValue;
        }

        @Override
        public Type type() {
            return Type.REFERENCE;
        }
    }
}
