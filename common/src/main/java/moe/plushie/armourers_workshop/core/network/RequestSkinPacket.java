package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.server.level.ServerPlayer;

public class RequestSkinPacket extends CustomPacket {

    private final String identifier;

    public RequestSkinPacket(String identifier) {
        this.identifier = identifier;
    }

    public RequestSkinPacket(IFriendlyByteBuf buffer) {
        this.identifier = buffer.readUtf();
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeUtf(identifier);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        ModLog.debug("'{}' => accept skin request", identifier);
        SkinLoader.getInstance().loadSkin(identifier, (skin, exception) -> {
            ModLog.debug("'{}' => response skin data, exception: {}", identifier, exception);
            ResponseSkinPacket packet = new ResponseSkinPacket(identifier, skin, exception);
            NetworkManager.sendTo(packet, player);
        });
    }
}
