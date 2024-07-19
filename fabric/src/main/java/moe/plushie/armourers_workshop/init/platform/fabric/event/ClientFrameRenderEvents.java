package moe.plushie.armourers_workshop.init.platform.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

public class ClientFrameRenderEvents {

    public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, context -> {
    }, callbacks -> context -> {
        for (final Start callback : callbacks) {
            callback.onStart(context);
        }
    });

    public static final Event<End> END = EventFactory.createArrayBacked(End.class, context -> {
    }, callbacks -> context -> {
        for (final End callback : callbacks) {
            callback.onEnd(context);
        }
    });

    @FunctionalInterface
    public interface Start {
        void onStart(Minecraft minecraft);
    }

    @FunctionalInterface
    public interface End {
        void onEnd(Minecraft minecraft);
    }
}
