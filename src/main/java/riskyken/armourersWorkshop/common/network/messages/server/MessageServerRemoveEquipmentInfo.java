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
public class MessageServerRemoveEquipmentInfo implements IMessage, IMessageHandler<MessageServerRemoveEquipmentInfo, IMessage> {

    PlayerPointer playerPointer;
    
    public MessageServerRemoveEquipmentInfo() {}
    
    public MessageServerRemoveEquipmentInfo(PlayerPointer playerPointer) {
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
    public IMessage onMessage(MessageServerRemoveEquipmentInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.removeEquipmentData(message.playerPointer);
        return null;
    }
}
