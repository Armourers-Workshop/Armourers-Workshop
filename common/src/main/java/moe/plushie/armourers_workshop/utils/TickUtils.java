package moe.plushie.armourers_workshop.utils;

public class TickUtils {

    private static boolean isPaused;
    private static long pausedTime;
    private static long ignoredTime;

    private static long time() {
        if (isPaused) {
            return pausedTime - ignoredTime;
        }
        return System.currentTimeMillis() - ignoredTime;
    }

    public static int ticks() {
        return (int) (time() % 100000000);
    }

    public static float getPaintTextureOffset() {
        double f = ticks() % (255L * 25) / 25f;
        return Math.round(f);
    }

    public static void tick(boolean newValue) {
        if (isPaused != newValue) {
            isPaused = newValue;
            if (newValue) {
                pausedTime = System.currentTimeMillis();
            } else {
                ignoredTime += System.currentTimeMillis() - pausedTime;
            }
        }
    }
}
