package moe.plushie.armourers_workshop.library.network;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import net.minecraft.server.level.ServerPlayer;

public class UploadSkinPacket extends CustomPacket {


    public UploadSkinPacket() {
    }

    public UploadSkinPacket(IFriendlyByteBuf buffer) {
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        if (player.containerMenu instanceof GlobalSkinLibraryMenu) {
            GlobalSkinLibraryMenu container = (GlobalSkinLibraryMenu) player.containerMenu;
            container.crafting();
        }
    }
}
