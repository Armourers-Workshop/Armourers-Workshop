package moe.plushie.armourers_workshop.utils;

import java.security.SecureRandom;

public class SkinUUID {

    /**
     * Random object used by random method. This has to be not local to the
     * random method to not return the same value in the same millisecond.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final char[] ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890".toCharArray();

    private final String value;

    public SkinUUID() {
        this(RANDOM.nextLong());
    }

    public SkinUUID(long... values) {
        int idx = 0;
        var words = new int[10];
        for (long value : values) {
            while (value != 0) {
                words[idx % 10] += (int) (value % 62);
                value /= 62;
                idx += 1;
            }
        }
        var builder = new StringBuilder(10);
        for (int word : words) {
            builder.append(ALPHABET[Math.abs(word % 62)]);
        }
        this.value = builder.reverse().toString();
    }

    public SkinUUID(String value) {
        this.value = value;
    }

    public static SkinUUID randomUUID() {
        return new SkinUUID();
    }

    public static String randomUUIDString() {
        return randomUUID().toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinUUID that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
