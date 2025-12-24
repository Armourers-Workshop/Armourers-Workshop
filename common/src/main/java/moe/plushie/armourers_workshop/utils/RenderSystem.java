package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.compat.client.renderer.AbstractRenderSystem;

import java.util.concurrent.atomic.AtomicInteger;

public final class RenderSystem extends AbstractRenderSystem {

    private static Runnable drawElementsCallback;
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


    public static void setDrawElementsCallback(Runnable runnable) {
        drawElementsCallback = runnable;
    }

    public static Runnable getDrawElementsCallback() {
        return drawElementsCallback;
    }
}
