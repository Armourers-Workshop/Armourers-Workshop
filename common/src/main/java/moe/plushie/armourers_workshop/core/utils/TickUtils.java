package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.common.IDeltaTracker;

public class TickUtils {

    private static final Timer TIMER = new Timer();

    public static void tick(IDeltaTracker deltaTracker) {
        TIMER.tick(deltaTracker);
    }

    public static void setTime(long time) {
        TIMER.setTime(time);
    }

    public static void setSpeed(float speed) {
        TIMER.setSpeed(speed);
    }

    public static double animationTick() {
        return TIMER.animationTick;
    }

    public static double animationTickByTime(long time) {
        // (server ticks - client current ticks) / 1000 + client animation ticks
        var delta = (time - TIMER.clock.time()) / 1000.0;
        return TIMER.animationTick + delta;
    }

    private static class Timer {

        private final OpenClock clock = new OpenClock();

        private double baseTicks = 0;
        private double animationTick = 0;

        private double modulator = 0.001;

        private long serverTime = 0;
        private long lastTime = 0;

        public void tick(IDeltaTracker deltaTracker) {
            var time = clock.time();
            var delta = (time - lastTime) * deltaTracker.rate() * modulator;
            if (delta > 1.0) {
                delta %= 1.0; // skipping
            }
            animationTick += delta;
            lastTime = time;
        }

        public void setTime(long time) {
            this.clock.setTime(time);
            this.serverTime = time;
            this.animationTick = 0.0; // always start by 0s.
            this.lastTime = time;
        }

        public void setSpeed(float speed) {
            this.modulator = speed / 1000.0;
        }
    }
}
