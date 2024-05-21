package moe.plushie.armourers_workshop.core.client.molang.math;

public class Timer {

    private final long duration;
    private boolean enabled;
    private long time;

    public Timer(long duration) {
        this.duration = duration;
    }

    public long getRemaining() {
        return this.time - System.currentTimeMillis();
    }

    public void mark() {
        this.mark(this.duration);
    }

    public void mark(long duration) {
        this.enabled = true;
        this.time = System.currentTimeMillis() + duration;
    }

    public void reset() {
        this.enabled = false;
    }

    public boolean checkReset() {
        boolean isEnabled = this.check();

        if (isEnabled) {
            this.reset();
        }

        return isEnabled;
    }

    public boolean check() {
        return this.enabled && this.isTime();
    }

    public boolean isTime() {
        return System.currentTimeMillis() >= this.time;
    }

    public boolean checkRepeat() {
        if (!this.enabled) {
            this.mark();
        }

        return this.checkReset();
    }
}
