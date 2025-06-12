package moe.plushie.armourers_workshop.core.utils;

public class OpenClock {

    private long base = 0;
    private long offset = 0;

    public void setTime(long time) {
        this.base = time;
        this.offset = time - currentMilliseconds();
    }

    public long time() {
        return offset + currentMilliseconds();
    }

    private long currentMilliseconds() {
        // we use System.nanoTime() instead of System.currentTimeMillis(),
        // because System.currentTimeMillis() have big fluctuations (>5ms).
        return System.nanoTime() / 1000000L;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "base", base, "time", time());
    }
}
