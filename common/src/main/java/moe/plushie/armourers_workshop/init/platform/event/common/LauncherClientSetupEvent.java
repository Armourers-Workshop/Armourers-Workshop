package moe.plushie.armourers_workshop.init.platform.event.common;

public interface LauncherClientSetupEvent {

    void enqueueWork(Runnable work);
}
