package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.api.action.IUndoCommand;
import moe.plushie.armourers_workshop.core.permission.Permissions;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.undo.UndoManager;
import moe.plushie.armourers_workshop.utils.undo.UndoStack;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.text.ITextComponent;

public class UndoActionPacket extends CustomPacket {

    private final boolean isRedo;

    public UndoActionPacket(PacketBuffer buffer) {
        this.isRedo = buffer.readBoolean();
    }

    public UndoActionPacket(boolean isRedo) {
        this.isRedo = isRedo;
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeBoolean(isRedo);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // TODO: check player
        try {
            ITextComponent message;
            UndoStack stack = UndoManager.of(player.getUUID());
            if (isRedo) {
                if (!Permissions.REDO.accept(player)) {
                    return;
                }
                IUndoCommand command = stack.redo();
                message = TranslateUtils.title("chat.armourers_workshop.undo.redoing", command.name());
            } else {
                if (!Permissions.UNDO.accept(player)) {
                    return;
                }
                IUndoCommand command = stack.undo();
                message = TranslateUtils.title("chat.armourers_workshop.undo.undoing", command.name());
            }
            player.sendMessage(message, player.getUUID());
        } catch (CommandException exception) {
            player.sendMessage(exception.getComponent(), player.getUUID());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
