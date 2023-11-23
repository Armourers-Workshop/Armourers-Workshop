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
        StringBuilder builder = new StringBuilder(10);
        long number = RANDOM.nextLong(Long.MAX_VALUE);
        for (int i = 0; i < 10; ++i) {
            builder.append(ALPHABET[(int) (number % 62)]);
            number /= 62;
        }
        this.value = builder.reverse().toString();
    }

    public SkinUUID(String value) {
        this.value = value;
    }

    public static SkinUUID randomUUID() {
        return new SkinUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinUUID)) return false;
        SkinUUID skinUUID = (SkinUUID) o;
        return value.equals(skinUUID.value);
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
