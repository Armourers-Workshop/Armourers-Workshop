package riskyken.armourersWorkshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerLibrarySendSkin implements IMessage, IMessageHandler<MessageServerLibrarySendSkin, IMessage> {

    private String fileName;
    private Skin skin;
    
    public MessageServerLibrarySendSkin() {
    }
    
    public MessageServerLibrarySendSkin(String fileName, Skin skin) {
        this.fileName = fileName;
        this.skin = skin;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.fileName = ByteBufUtils.readUTF8String(buf);
        this.skin = new Skin(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.fileName);
        skin.writeToBuf(buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerLibrarySendSkin message, MessageContext ctx) {
        ArmourersWorkshop.proxy.receivedSkinFromLibrary(message.fileName, message.skin);
        return null;
    }
}
