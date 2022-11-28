package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.data.undo.UndoManager;
import moe.plushie.armourers_workshop.builder.data.undo.UndoStack;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class UndoActionPacket extends CustomPacket {

    private final boolean isRedo;

    public UndoActionPacket(FriendlyByteBuf buffer) {
        this.isRedo = buffer.readBoolean();
    }

    public UndoActionPacket(boolean isRedo) {
        this.isRedo = isRedo;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(isRedo);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        // TODO: check player
        try {
            Component message;
            UndoStack stack = UndoManager.of(player.getUUID());
            if (isRedo) {
                if (!ModPermissions.REDO.accept(player)) {
                    return;
                }
                IUndoCommand command = stack.redo();
                message = TranslateUtils.title("chat.armourers_workshop.undo.redoing", command.name());
            } else {
                if (!ModPermissions.UNDO.accept(player)) {
                    return;
                }
                IUndoCommand command = stack.undo();
                message = TranslateUtils.title("chat.armourers_workshop.undo.undoing", command.name());
            }
            player.sendSystemMessage(message, player.getUUID());
        } catch (CommandRuntimeException exception) {
            player.sendSystemMessage(exception.getComponent(), player.getUUID());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
