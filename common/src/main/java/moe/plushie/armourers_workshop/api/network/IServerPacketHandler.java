package moe.plushie.armourers_workshop.api.network;

public interface IServerPacketHandler {

    void enqueueWork(Runnable work);
}
