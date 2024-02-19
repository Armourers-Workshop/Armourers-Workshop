package moe.plushie.armourers_workshop.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPacketDistributor {

    IPacketDistributor add(ResourceLocation channel, FriendlyByteBuf buf);

    void execute();

    boolean isClientbound();
}
