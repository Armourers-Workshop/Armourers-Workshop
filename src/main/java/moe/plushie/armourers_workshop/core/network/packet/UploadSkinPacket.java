package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.library.container.GlobalSkinLibraryContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

public class UploadSkinPacket extends CustomPacket {


    public UploadSkinPacket() {
    }

    public UploadSkinPacket(PacketBuffer buffer) {
    }

    @Override
    public void encode(PacketBuffer buffer) {
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        if (player.containerMenu instanceof GlobalSkinLibraryContainer) {
            GlobalSkinLibraryContainer container = (GlobalSkinLibraryContainer)player.containerMenu;
            container.crafting();
        }
    }
}
