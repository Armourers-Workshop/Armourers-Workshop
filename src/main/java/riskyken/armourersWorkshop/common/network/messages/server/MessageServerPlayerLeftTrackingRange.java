package riskyken.armourersWorkshop.common.network.messages.server;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.proxies.ClientProxy;

/**
 * Sent from the server to a client to let them know that a
 * player has left tracking range.
 * @author RiskyKen
 *
 */
public class MessageServerPlayerLeftTrackingRange implements IMessage, IMessageHandler<MessageServerPlayerLeftTrackingRange, IMessage> {

    PlayerPointer playerPointer;
    
    public MessageServerPlayerLeftTrackingRange() {}
    
    public MessageServerPlayerLeftTrackingRange(PlayerPointer playerPointer) {
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
    public IMessage onMessage(MessageServerPlayerLeftTrackingRange message, MessageContext ctx) {
        playerLeftTracking(playerPointer);
        return null;
    }
    
    private void playerLeftTracking(PlayerPointer playerPointer) {
        ClientProxy.playerLeftTrackingRange(playerPointer);
    }
}
