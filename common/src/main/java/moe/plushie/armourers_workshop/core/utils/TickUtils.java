package moe.plushie.armourers_workshop.core.utils;

public class TickUtils {

    private static final Timer TIMER = new Timer();

    public static void tick(boolean isPaused) {
        TIMER.tick(isPaused);
    }

    public static void setTime(long time) {
        TIMER.setTime(time);
    }

    public static void setSpeed(float speed) {
        TIMER.setSpeed(speed);
    }

    public static double animationTicks() {
        return TIMER.animationTicks;
    }

    public static double animationTicksByTime(long time) {
        // (server ticks - client current ticks) / 1000 + client animation ticks
        var delta = (time - TIMER.clock.time()) / 1000.0;
        return TIMER.animationTicks + delta;
    }

    private static class Timer {

        private final OpenClock clock = new OpenClock();

        private double baseTicks = 0;
        private double animationTicks = 0;

        private double modulator = 0.001;

        private long serverTime = 0;
        private long lastTime = 0;
        private boolean isPaused = false;

        public void tick(boolean isPaused) {
            // when status is changed, reset the ticks.
            if (this.isPaused != isPaused) {
                this.isPaused = isPaused;
                this.reset();
                return;
            }
            // when the tick is paused, ignore.
            if (isPaused) {
                return;
            }
            var time = clock.time();
            var delta = (time - lastTime) * modulator;
            animationTicks = baseTicks + delta;
        }

        public void setTime(long time) {
            this.clock.setTime(time);
            this.serverTime = time;
            this.animationTicks = 0.0; // always start by 0s.
            this.reset();
        }

        public void setSpeed(float speed) {
            this.modulator = speed / 1000.0;
            this.reset();
        }

        private void reset() {
            baseTicks = animationTicks;
            lastTime = clock.time();
        }
    }
}
