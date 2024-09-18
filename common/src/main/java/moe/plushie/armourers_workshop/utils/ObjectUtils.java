package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.math.IMatrix4f;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class ObjectUtils {

    private static final FloatBuffer BUFFER3x3 = createFloatBuffer(9);
    private static final FloatBuffer BUFFER4x4 = createFloatBuffer(16);

    public static <S, T> T unsafeCast(S src) {
        // noinspection unchecked
        return (T) src;
    }

    @Nullable
    public static <S, T> T safeCast(S src, Class<T> type) {
        if (type.isInstance(src)) {
            return type.cast(src);
        }
        return null;
    }

    public static void safeClose(AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static String readableName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        if (simpleName.matches("^I[A-Z].+$") && clazz.isInterface()) {
            simpleName = simpleName.substring(1);
        }
        return simpleName.replaceAll("([a-z]+)([A-Z]+)", "$1 $2");
    }

    public static <K, V> Map<K, V> toMap(K key, V value) {
        HashMap<K, V> map = new HashMap<>(1);
        map.put(key, value);
        return map;
    }

    public static <K, V> void difference(Map<K, V> oldValue, Map<K, V> newValue, BiConsumer<K, V> removeHandler, BiConsumer<K, V> insertHandler) {
        HashMap<K, V> insertEntities = new HashMap<>();
        HashMap<K, V> removedEntities = new HashMap<>(oldValue);
        newValue.forEach((key, value) -> {
            if (removedEntities.remove(key) == null) {
                insertEntities.put(key, value);
            }
        });
        removedEntities.forEach(removeHandler);
        insertEntities.forEach(insertHandler);
    }

    public static <K, V, R> V find(Map<K, V> map, R req, Function<K, R> resolver) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (req.equals(resolver.apply(entry.getKey()))) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static <V> V orElse(@Nullable V value, V defaultValue) {
        if (value != null) {
            return value;
        }
        return defaultValue;
    }


    public static <S, T> ArrayList<T> map(S[] in, Function<? super S, T> transform) {
        ArrayList<T> results = new ArrayList<>(in.length);
        for (S value : in) {
            results.add(transform.apply(value));
        }
        return results;
    }

    public static <S, T> ArrayList<T> map(Collection<S> in, Function<S, T> transform) {
        ArrayList<T> results = new ArrayList<>(in.size());
        for (S value : in) {
            results.add(transform.apply(value));
        }
        return results;
    }

    public static <S, T> ArrayList<T> compactMap(S[] in, Function<? super S, @Nullable T> transform) {
        ArrayList<T> results = new ArrayList<>(in.length);
        for (S value : in) {
            T res = transform.apply(value);
            if (res != null) {
                results.add(res);
            }
        }
        return results;
    }

    public static <S, T> ArrayList<T> compactMap(Collection<S> in, Function<? super S, @Nullable T> transform) {
        ArrayList<T> results = new ArrayList<>(in.size());
        for (S value : in) {
            T res = transform.apply(value);
            if (res != null) {
                results.add(res);
            }
        }
        return results;
    }

    @Nullable
    public static <S, T> T flatMap(@Nullable S src, Function<S, T> consumer) {
        if (src != null) {
            return consumer.apply(src);
        }
        return null;
    }

    public static <S, V> V flatMap(@Nullable S obj, Function<S, V> getter, V defaultValue) {
        if (obj != null) {
            V value = getter.apply(obj);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    public static <S, T> ArrayList<T> flatMap(@Nullable Collection<S> in, Function<? super S, @Nullable T> transform) {
        if (in != null) {
            ArrayList<T> results = new ArrayList<>(in.size());
            for (S value : in) {
                T res = transform.apply(value);
                if (res != null) {
                    results.add(res);
                }
            }
            return results;
        }
        return null;
    }

    public static <T> ArrayList<T> filter(T[] in, Predicate<? super T> predicate) {
        ArrayList<T> results = new ArrayList<>(in.length);
        for (T value : in) {
            if (predicate.test(value)) {
                results.add(value);
            }
        }
        return results;
    }

    public static <T> ArrayList<T> filter(Collection<T> in, Predicate<? super T> predicate) {
        ArrayList<T> results = new ArrayList<>(in.size());
        for (T value : in) {
            if (predicate.test(value)) {
                results.add(value);
            }
        }
        return results;
    }

    public static <T> void search(Collection<T> collection, Function<T, Collection<T>> children, Consumer<T> consumer) {
        for (T value : collection) {
            consumer.accept(value);
            search(children.apply(value), children, consumer);
        }
    }

    @SafeVarargs
    public static <T> ArrayList<T> map(T... objects) {
        ArrayList<T> results = new ArrayList<>(objects.length);
        Collections.addAll(results, objects);
        return results;
    }

    // "<%s: 0x%x; arg1 = arg2; ...; argN-1 = argN>"
    public static String makeDescription(Object obj, Object... arguments) {
        StringBuilder builder = new StringBuilder();
        builder.append("<");
        builder.append(getClassName(obj.getClass()));
        builder.append(": ");
        builder.append(String.format("0x%x", System.identityHashCode(obj)));
        for (int i = 0; i < arguments.length; i += 2) {
            if (isEmptyOrNull(arguments[i + 1])) {
                continue;
            }
            builder.append("; ");
            builder.append(arguments[i]);
            builder.append(" = ");
            builder.append(arguments[i + 1]);
        }
        builder.append(">");
        return builder.toString();
    }

    public static boolean isEmptyOrNull(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Collection<?>) {
            return ((Collection<?>) value).isEmpty();
        }
        if (value instanceof String) {
            return ((String) value).isEmpty();
        }
        return false;
    }

    public static String getClassName(Class<?> clazz) {
        String name = clazz.getTypeName();
        Package pkg = clazz.getPackage();
        if (pkg != null) {
            return name.replace(pkg.getName() + ".", "");
        }
        return clazz.getSimpleName();
    }

    public static <S> void ifPresent(@Nullable S src, Consumer<S> consumer) {
        if (src != null) {
            consumer.accept(src);
        }
    }

    public static void set(IMatrix3f matrixIn, IMatrix3f matrixOut) {
        matrixIn.store(BUFFER3x3);
        matrixOut.load(BUFFER3x3);
    }

    public static void set(IMatrix4f matrixIn, IMatrix4f matrixOut) {
        matrixIn.store(BUFFER4x4);
        matrixOut.load(BUFFER4x4);
    }

    public static <T> Collection<T> makeItems(int size, Function<Integer, T> builder) {
        ArrayList<T> results = new ArrayList<>();
        results.ensureCapacity(size);
        for (int i = 0; i < size; ++i) {
            results.add(builder.apply(i));
        }
        return results;
    }

    public static ByteBuffer createByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer createFloatBuffer(int capacity) {
        return createByteBuffer(Float.BYTES * capacity).asFloatBuffer();
    }

    public static <T> int sum(Collection<T> elements, ToIntFunction<T> getter) {
        int sum = 0;
        for (T element : elements) {
            sum += getter.applyAsInt(element);
        }
        return sum;
    }

    public static String dumpStackTrace() {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (StackTraceElement st : ste) {
            sb.append(st.toString()).append(System.lineSeparator());
        }
        return sb.toString();
    }


    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data        a byte[] to convert to Hex characters
     * @param toLowerCase {@code true} converts to lowercase, {@code false} to uppercase
     * @return A char[] containing hexadecimal characters in the selected case
     */
    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        final int dataLen = data.length;
        final char[] out = new char[dataLen << 1];
        final int dataOffset = 0;
        final int outOffset = 0;

        String toDigits = "0123456789ABCDEF";
        if (toLowerCase) {
            toDigits = "0123456789abcdef";
        }

        // two characters form the hex value.
        for (int i = dataOffset, j = outOffset; i < dataOffset + dataLen; i++) {
            out[j++] = toDigits.charAt((0xF0 & data[i]) >>> 4);
            out[j++] = toDigits.charAt(0x0F & data[i]);
        }
        return out;
    }

    /**
     * Converts an array of characters representing hexadecimal values into an array of bytes of those same values. The
     * returned array will be half the length of the passed array, as it takes two characters to represent any given
     * byte. An exception is thrown if the passed char array has an odd number of elements.
     *
     * @param data An array of characters containing hexadecimal digits
     * @return A byte array containing binary data decoded from the supplied char array.
     * @throws IllegalArgumentException Thrown if an odd number of characters or illegal characters are supplied
     */
    public static byte[] decodeHex(final char[] data) throws IllegalArgumentException {
        final int len = data.length;
        final int outOffset = 0;
        final byte[] out = new byte[len >> 1];

        if ((len & 0x01) != 0) {
            throw new IllegalArgumentException("Odd number of characters.");
        }

        // two characters form the hex value.
        for (int i = outOffset, j = 0; j < len; i++) {
            int f1 = Character.digit(data[j], 16);
            int f2 = Character.digit(data[j + 1], 16);
            if (f1 == -1) {
                throw new IllegalArgumentException("Illegal hexadecimal character " + f1 + " at index " + j);
            }
            if (f2 == -1) {
                throw new IllegalArgumentException("Illegal hexadecimal character " + f2 + " at index " + j + 1);
            }
            int f = (f1 << 4) | f2;
            out[i] = (byte) (f & 0xFF);
            j += 2;
        }

        return out;
    }

    public static String md5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] sig = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return new String(encodeHex(sig, true));
        } catch (Exception e) {
            e.printStackTrace();
            return value;
        }
    }
}
