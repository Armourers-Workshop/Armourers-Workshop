package moe.plushie.armourers_workshop.core.utils;

import java.security.SecureRandom;

public class OpenUUID {

    /**
     * Random object used by random method. This has to be not local to the
     * random method to not return the same value in the same millisecond.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int LENGTH = 10;
    private static final char[] ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890".toCharArray();

    private final String value;

    public OpenUUID() {
        this(RANDOM.nextLong());
    }

    public OpenUUID(long... values) {
        var idx = 0;
        var base = ALPHABET.length;
        var words = new int[LENGTH];
        for (var value : values) {
            while (value != 0) {
                words[idx % LENGTH] += (int) (value % base);
                value /= base;
                idx += 1;
            }
        }
        var builder = new StringBuilder(LENGTH);
        for (var word : words) {
            builder.append(ALPHABET[Math.abs(word % base)]);
        }
        this.value = builder.reverse().toString();
    }

    public OpenUUID(String value) {
        this.value = value;
    }

    public static OpenUUID randomUUID() {
        return new OpenUUID();
    }

    public static String randomUUIDString() {
        return randomUUID().toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenUUID that)) return false;
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
