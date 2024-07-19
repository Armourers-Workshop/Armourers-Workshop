package moe.plushie.armourers_workshop.utils;


import moe.plushie.armourers_workshop.core.data.TickTracker;

public class TickUtils {

    private static final TickTracker TRACKER = TickTracker.client();

    public static void tick(boolean isPaused)  {
        TRACKER.update(isPaused);
    }

    public static float animationTicks() {
        return TRACKER.animationTicks();
    }
}
