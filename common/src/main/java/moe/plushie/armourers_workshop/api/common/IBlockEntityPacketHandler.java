package moe.plushie.armourers_workshop.api.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockEntityPacketHandler {

    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     */
    void handleUpdatePacket(BlockState state, CompoundTag tag);
}
