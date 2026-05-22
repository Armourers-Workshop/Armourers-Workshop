package moe.plushie.armourers_workshop.core.skin.molang.core;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Extension of {@link Result} that provides memory optimization for computed results.
 * The primary difference from Result is that it performs memory optimizations on computed
 * values through object reuse, reducing allocation overhead during expression evaluation.
 * This is particularly useful for frequently computed intermediate values in MoLang expressions.
 */
public abstract class ComputedResult extends Result {

    public static Result newArray() {
        return Result.newArray();
    }

    public static Result newStruct() {
        return Result.newStruct();
    }

    public static Result valueOf(boolean value) {
        return Result.valueOf(value);
    }

    public static Result valueOf(double value) {
        return NumberValue.newInstance(value);
    }

    public static Result valueOf(String value) {
        return StringValue.newInstance(value);
    }

    public static Result valueOf(List<Result> value) {
        return ArrayValue.newInstance(value);
    }

    public static Result valueOf(Map<Name, Result> value) {
        return StructValue.newInstance(value);
    }

    public static Result wrap(Object reference) {
        return Result.wrap(reference);
    }

    public static void beginCaching() {
        Pool.NUMBER.begin();
        Pool.STRING.begin();
        Pool.ARRAY.begin();
        Pool.STRUCT.begin();
    }

    public static void endCaching() {
        Pool.NUMBER.end();
        Pool.STRING.end();
        Pool.ARRAY.end();
        Pool.STRUCT.end();
    }

    private static class NumberValue extends Result {

        private double wrappedValue;

        private static NumberValue newInstance(double wrappedValue) {
            var that = Pool.NUMBER.alloc();
            that.wrappedValue = wrappedValue;
            return that;
        }

        @Override
        public Result copy() {
            return Result.valueOf(wrappedValue);
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

        private String wrappedValue;

        private static StringValue newInstance(String wrappedValue) {
            var that = Pool.STRING.alloc();
            that.wrappedValue = wrappedValue;
            return that;
        }

        @Override
        public Result copy() {
            return Result.valueOf(wrappedValue);
        }

        @Override
        public String stringValue() {
            return wrappedValue;
        }

        @Override
        public Type type() {
            return Type.STRING;
        }
    }

    private static class ArrayValue extends Result {

        private List<Result> wrappedValue;

        private static ArrayValue newInstance(List<Result> wrappedValue) {
            var that = Pool.ARRAY.alloc();
            that.wrappedValue = wrappedValue;
            return that;
        }

        @Override
        public Result copy() {
            return Result.valueOf(wrappedValue);
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

        private Map<Name, Result> wrappedValue;

        private static StructValue newInstance(Map<Name, Result> wrappedValue) {
            var that = Pool.STRUCT.alloc();
            that.wrappedValue = wrappedValue;
            return that;
        }

        @Override
        public Result copy() {
            return Result.valueOf(wrappedValue);
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

    private static class Pool<T> {

        private static final Pool<NumberValue> NUMBER = new Pool<>(NumberValue::new);
        private static final Pool<StringValue> STRING = new Pool<>(StringValue::new);
        private static final Pool<ArrayValue> ARRAY = new Pool<>(ArrayValue::new);
        private static final Pool<StructValue> STRUCT = new Pool<>(StructValue::new);

        private final Supplier<T> factory;

        private ArrayDeque<T> using = new ArrayDeque<>();
        private ArrayDeque<T> pending = new ArrayDeque<>();

        private int depth = 0;

        private Pool(Supplier<T> factory) {
            this.factory = factory;
        }

        public T alloc() {
            if (depth <= 0) {
                return factory.get();
            }
            var value = using.poll();
            if (value == null) {
                value = factory.get();
            }
            pending.push(value);
            return value;
        }

        public void begin() {
            depth += 1;
        }

        public void end() {
            depth -= 1;
            if (depth != 0) {
                return;
            }
            var used = pending.size();
            var remaining = using.size();
            var oldValue = using;
            using = pending;
            pending = oldValue;
            // too much left
            if (remaining > used * 2) {
                oldValue.clear();
            }
        }
    }
}
