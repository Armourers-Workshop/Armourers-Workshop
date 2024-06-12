package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.AdvancedBuilderMenu;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModPermissions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AdvancedExportPacket extends CustomPacket {

    private final BlockPos pos;

    public AdvancedExportPacket(AdvancedBuilderBlockEntity blockEntity) {
        this.pos = blockEntity.getBlockPos();
    }

    public AdvancedExportPacket(IFriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // this is an unauthorized operation, ignore it
        BlockEntity blockEntity = player.getLevel().getBlockEntity(pos);
        if (!(blockEntity instanceof AdvancedBuilderBlockEntity blockEntity1) || !(player.containerMenu instanceof AdvancedBuilderMenu)) {
            abort(player, "unauthorized", "user status is incorrect");
            return;
        }
        if (!ModPermissions.ADVANCED_SKIN_BUILDER_SKIN_EXPORT.accept(player)) {
            abort(player, "export", "prohibited by the config file");
            return;
        }
        accept(player, "export");
        //        String identifier = SkinLoader.getInstance().saveSkin("fs:", skin);
        blockEntity1.exportFromDocument(player);
    }

    private void accept(Player player, String op) {
        String playerName = player.getScoreboardName();
        ModLog.info("accept {} request of the '{}'", op, playerName);
    }

    private void abort(Player player, String op, String reason) {
        String playerName = player.getScoreboardName();
        ModLog.info("abort {} request of the '{}', reason: '{}'", op, playerName, reason);
    }
}
