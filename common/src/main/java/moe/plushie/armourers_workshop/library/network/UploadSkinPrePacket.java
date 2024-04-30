package moe.plushie.armourers_workshop.library.network;

import moe.plushie.armourers_workshop.api.common.IResultHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.network.CustomReplyPacket;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.minecraft.server.level.ServerPlayer;

public class UploadSkinPrePacket extends CustomReplyPacket<Boolean> {

    public UploadSkinPrePacket() {
        super(DataSerializers.BOOLEAN);
    }

    public UploadSkinPrePacket(IFriendlyByteBuf buf) {
        super(DataSerializers.BOOLEAN, buf);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player, IResultHandler<Boolean> reply) {
        // check the user global skin upload permission.
        reply.accept(ModPermissions.SKIN_LIBRARY_GLOBAL_SKIN_UPLOAD.accept(player));
    }
}
