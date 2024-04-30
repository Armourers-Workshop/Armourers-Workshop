package moe.plushie.armourers_workshop.init.platform.event.common;

public interface LauncherCommonSetupEvent {

    void enqueueWork(Runnable work);
}
