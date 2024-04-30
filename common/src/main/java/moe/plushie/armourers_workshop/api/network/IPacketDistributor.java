package moe.plushie.armourers_workshop.api.network;

import net.minecraft.resources.ResourceLocation;

public interface IPacketDistributor {

    IPacketDistributor add(ResourceLocation channel, IFriendlyByteBuf buf);

    void execute();

    boolean isClientbound();
}
