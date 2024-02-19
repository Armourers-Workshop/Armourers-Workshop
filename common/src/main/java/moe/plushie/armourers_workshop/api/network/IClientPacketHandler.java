package moe.plushie.armourers_workshop.api.network;

public interface IClientPacketHandler {

    void enqueueWork(Runnable work);
}
