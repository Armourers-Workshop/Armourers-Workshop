package moe.plushie.armourers_workshop.core.utils;

import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class Objects {


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

    public static <S, T> T flatMap(S src, Function<S, T> consumer) {
        if (src != null) {
            return consumer.apply(src);
        }
        return null;
    }

    public static <S, V> V flatMap(S obj, Function<S, V> getter, V defaultValue) {
        if (obj != null) {
            V value = getter.apply(obj);
            if (value != null) {
                return value;
            }
        }
        return defaultValue;
    }

    public static <S> S compactMap(S value, S defaultValue) {
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    public static boolean equals(Object a, Object b) {
        return java.util.Objects.equals(a, b);
    }

    // "<%s: 0x%x; arg1 = arg2; ...; argN-1 = argN>"
    public static String toString(Object obj, Object... arguments) {
        var builder = new StringBuilder();
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

    private static String getClassName(Class<?> clazz) {
        var name = clazz.getTypeName();
        var pkg = clazz.getPackage();
        if (pkg != null) {
            return name.replace(pkg.getName() + ".", "");
        }
        return clazz.getSimpleName();
    }

    private static boolean isEmptyOrNull(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof Map<?, ?> map) {
            return map.isEmpty();
        }
        if (value instanceof Collection<?> collection) {
            return collection.isEmpty();
        }
        if (value instanceof String stringValue) {
            return stringValue.isEmpty();
        }
        return false;
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
