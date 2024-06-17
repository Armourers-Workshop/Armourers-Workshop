package moe.plushie.armourers_workshop.utils;

public class TickUtils {

    private static boolean isPaused;

    private static long pausedTime;
    private static long ignoredTime = System.nanoTime();

    private static long time() {
        if (isPaused) {
            return pausedTime - ignoredTime;
        }
        return System.nanoTime() - ignoredTime;
    }

    public static float animationTicks() {
        // nanoseconds to seconds.
        return time() / 1e9f;
    }

    public static void tick(boolean newValue) {
        if (isPaused != newValue) {
            isPaused = newValue;
            if (newValue) {
                pausedTime = System.nanoTime();
            } else {
                ignoredTime += System.nanoTime() - pausedTime;
            }
        }
    }

    public static void init() {
    }
}
