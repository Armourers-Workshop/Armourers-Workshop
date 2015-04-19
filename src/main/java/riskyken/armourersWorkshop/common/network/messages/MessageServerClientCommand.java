package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
        ArmourersWorkshop.proxy.receivedCommandFromSever(command);
        return null;
    }
    
    public enum CommandType {
        CLEAR_MODEL_CACHE;
    }
}
