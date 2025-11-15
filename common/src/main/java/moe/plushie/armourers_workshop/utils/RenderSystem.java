package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.compat.client.AbstractRenderSystem;

import java.util.concurrent.atomic.AtomicInteger;

public final class RenderSystem extends AbstractRenderSystem {

    private static final AtomicInteger extendedScissorFlags = new AtomicInteger();

    public static void safeCall(Runnable task) {
        if (isOnRenderThread()) {
            task.run();
        } else {
            recordRenderCall(task::run);
        }
    }


    public static void setExtendedScissorFlags(int flags) {
        extendedScissorFlags.set(flags);
    }

    public static int getExtendedScissorFlags() {
        return extendedScissorFlags.get();
    }
}
