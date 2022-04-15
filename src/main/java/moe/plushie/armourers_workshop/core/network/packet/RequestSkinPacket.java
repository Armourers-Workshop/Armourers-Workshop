package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.common.ModLog;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

public class RequestSkinPacket extends CustomPacket {

    private final String identifier;

    public RequestSkinPacket(String identifier) {
        this.identifier = identifier;
    }

    public RequestSkinPacket(PacketBuffer buffer) {
        this.identifier = buffer.readUtf();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(identifier);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        ModLog.debug("'{}' => accept skin request", identifier);
        SkinLoader.getInstance().loadSkin(identifier, (skin, exception) -> {
            ModLog.debug("'{}' => response skin data, exception: {}", identifier, exception);
            ResponseSkinPacket packet = new ResponseSkinPacket(identifier, skin, exception);
            NetworkHandler.getInstance().sendTo(packet, player);
        });
    }
}
