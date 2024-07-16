package moe.plushie.armourers_workshop.utils;

public class TickUtils {

    private static boolean runsNormally;

    private static long currentTime = System.nanoTime();
    private static long pausedTime;
    private static long ignoredTime = System.nanoTime();

    private static long time() {
        if (runsNormally) {
            return pausedTime - ignoredTime;
        }
        return currentTime - ignoredTime;
    }

    public static float animationTicks() {
        // nanoseconds to seconds.
        return time() / 1e9f;
    }

    public static void tick(boolean isPaused) {
        currentTime = System.nanoTime();
        if (runsNormally == isPaused) {
            return;
        }
        runsNormally = isPaused;
        if (isPaused) {
            pausedTime = currentTime;
        } else {
            ignoredTime += currentTime - pausedTime;
        }
    }

    public static void init() {
    }
}
