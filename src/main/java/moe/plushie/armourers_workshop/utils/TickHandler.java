package moe.plushie.armourers_workshop.utils;

public class TickHandler {

    private static long pausedTime;
    private static long ignoredTime;

    private static long getTime() {
        if (pausedTime != 0) {
            return pausedTime - ignoredTime;
        }
        return System.currentTimeMillis() - ignoredTime;
    }

    public static int ticks() {
        return (int)(getTime() % 100000000);
    }

    public static void pause() {
        pausedTime = System.currentTimeMillis();
    }

    public static void resume() {
        ignoredTime += System.currentTimeMillis() - pausedTime;
        pausedTime = 0;
    }
}
