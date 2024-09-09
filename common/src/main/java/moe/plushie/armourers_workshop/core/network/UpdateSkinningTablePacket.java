package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IClientPacketHandler;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.blockentity.SkinningTableBlockEntity;
import moe.plushie.armourers_workshop.core.menu.SkinningTableMenu;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class UpdateSkinningTablePacket extends CustomPacket {

    private final BlockPos pos;
    private final SkinDescriptor.Options options;

    public UpdateSkinningTablePacket(IFriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.options = new SkinDescriptor.Options(buffer.readNbt());
    }

    public UpdateSkinningTablePacket(SkinningTableBlockEntity entity, SkinDescriptor.Options options) {
        this.pos = entity.getBlockPos();
        this.options = options;
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeNbt(options.serializeNBT());
    }

    @Override
    public void accept(IClientPacketHandler packetHandler, Player player) {
        apply(player, false);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        apply(player, true);
    }

    private void apply(Player player, boolean needForwarding) {
        var entity = player.getLevel().getBlockEntity(pos);
        if (entity instanceof SkinningTableBlockEntity blockEntity && player.containerMenu instanceof SkinningTableMenu menu) {
            blockEntity.setOptions(options);
            menu.onCraftSlotChanges();
            if (needForwarding) {
                NetworkManager.sendToTrackingBlock(this, blockEntity);
            }
        }
    }
}
