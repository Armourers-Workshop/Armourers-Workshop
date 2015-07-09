package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.data.PlayerPointer;

/**
 * Sent from the server to a client to let them know that they
 * can discard the skin info for a player.
 * @author RiskyKen
 *
 */
public class MessageServerRemoveSkinInfo implements IMessage, IMessageHandler<MessageServerRemoveSkinInfo, IMessage> {

    PlayerPointer playerPointer;
    
    public MessageServerRemoveSkinInfo() {}
    
    public MessageServerRemoveSkinInfo(PlayerPointer playerPointer) {
        this.playerPointer = playerPointer;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerPointer = new PlayerPointer(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.playerPointer.writeToByteBuffer(buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerRemoveSkinInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.removeEquipmentData(message.playerPointer);
        return null;
    }
}
