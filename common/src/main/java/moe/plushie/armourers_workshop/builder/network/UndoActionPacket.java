package moe.plushie.armourers_workshop.builder.network;

import moe.plushie.armourers_workshop.api.action.IUserAction;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.builder.data.undo.UndoManager;
import moe.plushie.armourers_workshop.builder.data.undo.UndoStack;
import moe.plushie.armourers_workshop.builder.data.undo.action.ActionRuntimeException;
import moe.plushie.armourers_workshop.core.network.CustomPacket;
import moe.plushie.armourers_workshop.init.ModPermissions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class UndoActionPacket extends CustomPacket {

    private final boolean isRedo;

    public UndoActionPacket(IFriendlyByteBuf buffer) {
        this.isRedo = buffer.readBoolean();
    }

    public UndoActionPacket(boolean isRedo) {
        this.isRedo = isRedo;
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
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
                IUserAction command = stack.redo();
                message = Component.translatable("chat.armourers_workshop.undo.redoing", command.name());
            } else {
                if (!ModPermissions.UNDO.accept(player)) {
                    return;
                }
                IUserAction command = stack.undo();
                message = Component.translatable("chat.armourers_workshop.undo.undoing", command.name());
            }
            player.sendSystemMessage(message);
        } catch (ActionRuntimeException exception) {
            player.sendSystemMessage(exception.getComponent());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
