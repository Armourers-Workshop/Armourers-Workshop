package moe.plushie.armourers_workshop.api.network;

import net.minecraft.world.entity.player.Player;

public interface IClientPacketHandler {

    Player player();

    void enqueueWork(Runnable work);
}
