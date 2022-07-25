package moe.plushie.armourers_workshop.utils;


import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class SkinUUID {

    /**
     * Random object used by random method. This has to be not local to the
     * random method so as to not return the same value in the same millisecond.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private static final char[] ALPHABET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1234567890".toCharArray();

    /**
     * we designed skin uuid has 10 base62-encoded characters,
     * so we have 62^10 a number or 2^59 bits:
     * 6    0-6     random number
     * 12   6-18    sequence number
     * 41   18-59   timestamp
     */
    private final long value;

    public SkinUUID() {
        this.value = (System.currentTimeMillis() << 18) | (COUNTER.getAndIncrement() % (1 << 12)) << 6 | RANDOM.nextInt(1 << 6);
    }

    public static SkinUUID randomUUID() {
        return new SkinUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinUUID)) return false;
        SkinUUID skinUUID = (SkinUUID) o;
        return value == skinUUID.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(10);
        long number = value;
        for (int i = 0; i < 10; ++i) { //
            builder.append(ALPHABET[(int) (number % 62)]);
            number /= 62;
        }
        return builder.reverse().toString();
    }
}
