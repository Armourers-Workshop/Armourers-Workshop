package moe.plushie.armourers_workshop.api.network;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;

public interface IPacketDistributor {

    IPacketDistributor add(IResourceLocation channel, IFriendlyByteBuf buf);

    void execute();

    boolean isClientbound();
}
