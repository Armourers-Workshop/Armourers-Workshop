package moe.plushie.armourers_workshop.core.utils;

public class OpenPrimitive {

    public static final OpenPrimitive NULL = new OpenPrimitive(null);

    public static final OpenPrimitive TRUE = new OpenPrimitive(true);
    public static final OpenPrimitive FALSE = new OpenPrimitive(false);

    public static final OpenPrimitive INT_ZERO = new OpenPrimitive(0);
    public static final OpenPrimitive FLOAT_ZERO = new OpenPrimitive(0.0f);
    public static final OpenPrimitive DOUBLE_ZERO = new OpenPrimitive(0.0d);
    public static final OpenPrimitive EMPTY_STRING = new OpenPrimitive("");

    private final Object value;

    private OpenPrimitive(Object value) {
        this.value = value;
    }

    public static OpenPrimitive of(boolean value) {
        if (value) {
            return TRUE;
        }
        return FALSE;
    }

    public static OpenPrimitive of(byte value) {
        return new OpenPrimitive(value);
    }

    public static OpenPrimitive of(short value) {
        return new OpenPrimitive(value);
    }

    public static OpenPrimitive of(int value) {
        return new OpenPrimitive(value);
    }

    public static OpenPrimitive of(long value) {
        return new OpenPrimitive(value);
    }

    public static OpenPrimitive of(float value) {
        return new OpenPrimitive(value);
    }

    public static OpenPrimitive of(double value) {
        return new OpenPrimitive(value);
    }

    public static OpenPrimitive of(String value) {
        return new OpenPrimitive(value);
    }


    public OpenPrimitive negative() {
        if (value instanceof Boolean booleanValue) {
            return of(!booleanValue);
        }
        if (value instanceof Byte byteValue) {
            return of(-byteValue);
        }
        if (value instanceof Short shortValue) {
            return of(-shortValue);
        }
        if (value instanceof Integer integerValue) {
            return of(-integerValue);
        }
        if (value instanceof Long longValue) {
            return of(-longValue);
        }
        if (value instanceof Float floatValue) {
            return of(-floatValue);
        }
        if (value instanceof Double doubleValue) {
            return of(-doubleValue);
        }
        if (value instanceof String stringValue) {
            var fixed = "-(" + stringValue + ")";
            if (fixed.startsWith("-(-(") && fixed.endsWith("))")) {
                fixed = fixed.substring(4, fixed.length() - 2);
            }
            return of(fixed);
        }
        return this;
    }

    public boolean booleanValue() {
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof Byte byteValue) {
            return byteValue != 0;
        }
        if (value instanceof Short shortValue) {
            return shortValue != 0;
        }
        if (value instanceof Integer integerValue) {
            return integerValue != 0;
        }
        if (value instanceof Long longValue) {
            return longValue != 0;
        }
        if (value instanceof Float floatValue) {
            return floatValue != 0;
        }
        if (value instanceof Double doubleValue) {
            return doubleValue != 0;
        }
        if (value instanceof String stringValue) {
            return !stringValue.isEmpty();
        }
        return false;
    }

    public byte byteValue() {
        return numberValue().byteValue();
    }

    public short shortValue() {
        return numberValue().shortValue();
    }

    public int intValue() {
        return numberValue().intValue();
    }

    public long longValue() {
        return numberValue().longValue();
    }

    public float floatValue() {
        return numberValue().floatValue();
    }

    public double doubleValue() {
        return numberValue().doubleValue();
    }

    public String stringValue() {
        if (value instanceof Number numberValue) {
            return numberValue.toString();
        }
        if (value instanceof String stringValue) {
            return stringValue;
        }
        return "";
    }

    public Number numberValue() {
        if (value instanceof Number numberValue) {
            return numberValue;
        }
        return 0;
    }

    public Object rawValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenPrimitive that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        if (value != null) {
            return value.toString();
        }
        return "null";
    }

    public boolean isNull() {
        return value == null;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isString() {
        return value instanceof String;
    }
}
