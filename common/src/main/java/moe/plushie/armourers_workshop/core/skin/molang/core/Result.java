package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class Result implements BooleanSupplier, DoubleSupplier {

    public static final Result NULL = new Result(Type.NULL, 0, null, null, null, null);

    public static final Result ONE = new Result(Type.NUMBER, 1, null, null, null, null);
    public static final Result ZERO = new Result(Type.NUMBER, 0, null, null, null, null);

    private final Type type;

    private final double doubleValue;
    private final String stringValue;

    private final List<Result> arrayValue;
    private final Map<Name, Result> structValue;

    private final Object referenceValue;

    private Result(Type type, double doubleValue, String stringValue, List<Result> arrayValue, Map<Name, Result> structValue, Object referenceValue) {
        this.type = type;
        this.doubleValue = doubleValue;
        this.stringValue = stringValue;
        this.arrayValue = arrayValue;
        this.structValue = structValue;
        // ?
        this.referenceValue = referenceValue;
    }

    public static Result newArray() {
        return valueOf(new ArrayList<>());
    }

    public static Result newStruct() {
        return valueOf(new HashMap<>());
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
        return new Result(Type.NUMBER, value, null, null, null, null);
    }

    public static Result valueOf(String value) {
        return new Result(Type.STRING, 0, value, null, null, null);
    }

    public static Result valueOf(List<Result> array) {
        return new Result(Type.ARRAY, 0, null, array, null, null);
    }

    public static Result valueOf(Map<Name, Result> struct) {
        return new Result(Type.STRUCT, 0, null, null, struct, null);
    }

    public static Result wrap(Object reference) {
        return new Result(Type.REFERENCE, 0, null, null, null, reference);
    }

    public Result copy() {
        // is a array value?
        if (arrayValue != null) {
            var newValue = new ArrayList<Result>(arrayValue.size());
            for (var element : arrayValue) {
                newValue.add(element.copy()); // deep copy
            }
            return valueOf(newValue);
        }
        // is a struct value?
        if (structValue != null) {
            var newValue = new HashMap<Name, Result>(structValue.size());
            for (var element : structValue.entrySet()) {
                newValue.put(element.getKey(), element.getValue().copy()); // deep copy
            }
            return valueOf(newValue);
        }
        return this;
    }

    public boolean notEquals(Object o) {
        return !equals(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Result that)) return false;
        if (type != that.type) return false;
        return switch (type) {
            case NULL -> true;
            case NUMBER -> Double.compare(doubleValue, that.doubleValue) == 0;
            case STRING -> Objects.equals(stringValue, that.stringValue);
            case ARRAY -> Objects.equals(arrayValue, that.arrayValue);
            case STRUCT -> Objects.equals(structValue, that.structValue);
            case REFERENCE -> Objects.equals(referenceValue, that.referenceValue);
        };
    }

    @Override
    public int hashCode() {
        int hash = switch (type) {
            case NULL -> 0;
            case NUMBER -> Objects.hash(doubleValue);
            case STRING -> Objects.hash(stringValue);
            case ARRAY -> Objects.hash(arrayValue);
            case STRUCT -> Objects.hash(structValue);
            case REFERENCE -> Objects.hash(referenceValue);
        };
        hash = 31 * hash + type.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return switch (type) {
            case NULL -> "<null>";
            case NUMBER -> String.format("%s", doubleValue);
            case STRING -> String.format("%s", stringValue);
            case ARRAY -> String.format("%s", arrayValue);
            case STRUCT -> String.format("%s", structValue);
            case REFERENCE -> String.format("%s", referenceValue);
        };
    }

    @Override
    public double getAsDouble() {
        return doubleValue;
    }

    @Override
    public boolean getAsBoolean() {
        return doubleValue != 0.0;
    }

    public int getAsInt() {
        return MathHelper.floor(doubleValue);
    }

    public String getAsString() {
        if (stringValue != null) {
            return stringValue;
        }
        return toString();
    }

    public Object getAsReference() {
        return referenceValue;
    }

    public Type type() {
        return type;
    }

    public boolean isValid() {
        return type != Type.NULL;
    }

    public boolean isNull() {
        return type == Type.NULL;
    }

    public boolean isNumber() {
        return type == Type.NUMBER;
    }

    public boolean isString() {
        return type == Type.STRING;
    }

    public boolean isArray() {
        return type == Type.ARRAY;
    }

    public boolean isStruct() {
        return type == Type.STRUCT;
    }


    public void set(int index, Result value) {
        if (arrayValue != null && !arrayValue.isEmpty()) {
            arrayValue.set(Math.max(index, 0) % arrayValue.size(), value);
        }
    }

    public Result get(int index) {
        if (arrayValue != null && !arrayValue.isEmpty()) {
            return arrayValue.get(Math.max(index, 0) % arrayValue.size());
        }
        return Result.NULL;
    }

    public void set(Name name, Result value) {
        if (structValue != null) {
            structValue.put(name, value);
        }
    }

    public Result get(Name name) {
        if (structValue != null) {
            return structValue.getOrDefault(name, Result.NULL);
        }
        return Result.NULL;
    }

    public int size() {
        if (arrayValue != null) {
            return arrayValue.size();
        }
        if (structValue != null) {
            return structValue.size();
        }
        return 0;
    }

    public enum Type {
        NULL, NUMBER, STRING, ARRAY, STRUCT, REFERENCE
    }
}
