package moe.plushie.armourers_workshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerClientCommand implements IMessage, IMessageHandler<MessageServerClientCommand, IMessage> {

    private CommandType command;

    public MessageServerClientCommand() {
    }

    public MessageServerClientCommand(CommandType command) {
        this.command = command;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.command = CommandType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.command.ordinal());
    }

    @Override
    public IMessage onMessage(MessageServerClientCommand message, MessageContext ctx) {
        ArmourersWorkshop.getProxy().receivedCommandFromSever(message.command);
        return null;
    }

    public enum CommandType {
        CLEAR_MODEL_CACHE, OPEN_MOD_FOLDER
    }
}
