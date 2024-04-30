package moe.plushie.armourers_workshop.init.platform.event.common;

public interface LauncherLoadCompleteEvent {
    void enqueueWork(Runnable work);
}
