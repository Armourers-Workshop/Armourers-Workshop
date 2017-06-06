package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;

/**
 * Send from the server to the client when a client saves a skin
 * from the server to their local drive.
 * @author Riskyken
 *
 */
public class MessageServerLibrarySendSkin implements IMessage, IMessageHandler<MessageServerLibrarySendSkin, IMessage> {

    private String fileName;
    private String filePath;
    private Skin skin;
    
    public MessageServerLibrarySendSkin() {
    }
    
    public MessageServerLibrarySendSkin(String fileName, String filePath, Skin skin) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.skin = skin;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.fileName = ByteBufUtils.readUTF8String(buf);
        this.filePath = ByteBufUtils.readUTF8String(buf);
        this.skin = ByteBufHelper.readSkinFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.fileName);
        ByteBufUtils.writeUTF8String(buf, this.filePath);
        ByteBufHelper.writeSkinToByteBuf(buf, skin);
    }
    
    @Override
    public IMessage onMessage(MessageServerLibrarySendSkin message, MessageContext ctx) {
        ArmourersWorkshop.proxy.receivedSkinFromLibrary(message.fileName, message.filePath, message.skin);
        return null;
    }
}
