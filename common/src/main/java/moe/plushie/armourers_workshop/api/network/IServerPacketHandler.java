package moe.plushie.armourers_workshop.api.network;

import net.minecraft.server.level.ServerPlayer;

public interface IServerPacketHandler {

    ServerPlayer player();

    void enqueueWork(Runnable work);
}
