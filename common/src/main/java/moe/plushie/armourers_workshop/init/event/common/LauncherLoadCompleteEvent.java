package moe.plushie.armourers_workshop.init.event.common;

public interface LauncherLoadCompleteEvent {

    void enqueueWork(Runnable work);
}
