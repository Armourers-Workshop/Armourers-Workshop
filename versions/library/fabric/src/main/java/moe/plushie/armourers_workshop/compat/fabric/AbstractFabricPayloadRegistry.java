package moe.plushie.armourers_workshop.compat.fabric;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;

@Available("[21, 26)")
public class AbstractFabricPayloadRegistry {

    /**
     * The serverbound (client to server) play channel.
     */
    static PayloadTypeRegistry<RegistryFriendlyByteBuf> serverboundPlay() {
        return PayloadTypeRegistry.playC2S();
    }

    /**
     * The clientbound (server to client) play channel.
     */
    static PayloadTypeRegistry<RegistryFriendlyByteBuf> clientboundPlay() {
        return PayloadTypeRegistry.playS2C();
    }
}
