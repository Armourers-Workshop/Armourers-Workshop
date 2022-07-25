package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class UploadSkinPacket extends CustomPacket {


    public UploadSkinPacket() {
    }

    public UploadSkinPacket(FriendlyByteBuf buffer) {
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        if (player.containerMenu instanceof GlobalSkinLibraryMenu) {
            GlobalSkinLibraryMenu container = (GlobalSkinLibraryMenu) player.containerMenu;
            container.crafting();
        }
    }
}
