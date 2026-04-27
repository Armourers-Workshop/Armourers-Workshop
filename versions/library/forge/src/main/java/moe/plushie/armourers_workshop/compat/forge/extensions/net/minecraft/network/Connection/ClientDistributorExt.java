package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.network.Connection;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[21, 26)")
@Extension
public class ClientDistributorExt {

    public static void sendToServer(@ThisClass Class<?> clazz, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        PacketDistributor.sendToServer(payload, payloads);
    }
}
