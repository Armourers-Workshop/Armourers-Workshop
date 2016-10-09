package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;

 /**
  * Sent from the server to the client with the skin ID for a file name.
  * @author RiskyKen
  *
  */
public class MessageServerSkinIdSend implements IMessage, IMessageHandler<MessageServerSkinIdSend, IMessage> {
    
    private String fileName;
    private int skinId;
    
    public MessageServerSkinIdSend() {
    }
    
    public MessageServerSkinIdSend(String fileName, int skinId) {
        this.fileName = fileName;
        this.skinId = skinId;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(skinId);
        ByteBufUtils.writeUTF8String(buf, fileName);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        skinId = buf.readInt();
        fileName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public IMessage onMessage(MessageServerSkinIdSend message, MessageContext ctx) {
        setIdForFileName(message.fileName, message.skinId);
        return null;
    }
    
    private void setIdForFileName(String fileName, int skinId) {
        ClientSkinCache.INSTANCE.setIdForFileName(fileName, skinId);
    }
}
