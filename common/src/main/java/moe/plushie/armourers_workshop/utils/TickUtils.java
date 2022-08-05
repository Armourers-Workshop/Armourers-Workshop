package moe.plushie.armourers_workshop.utils;

public class TickUtils {

    private static long pausedTime;
    private static long ignoredTime;

    private static long getTime() {
        if (pausedTime != 0) {
            return pausedTime - ignoredTime;
        }
        return System.currentTimeMillis() - ignoredTime;
    }

    public static int ticks() {
        return (int) (getTime() % 100000000);
    }

    public static float getPaintTextureOffset() {
        double f = ticks() % (255L * 25) / 25f;
        return Math.round(f);
    }

    public static void pause() {
        pausedTime = System.currentTimeMillis();
    }

    public static void resume() {
        ignoredTime += System.currentTimeMillis() - pausedTime;
        pausedTime = 0;
    }
}
