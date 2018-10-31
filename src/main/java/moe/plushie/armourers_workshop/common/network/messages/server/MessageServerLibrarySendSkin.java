package moe.plushie.armourers_workshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.network.ByteBufHelper;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
    private SendType sendType;
    
    public MessageServerLibrarySendSkin() {
    }
    
    public MessageServerLibrarySendSkin(String fileName, String filePath, Skin skin, SendType sendType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.skin = skin;
        this.sendType = sendType;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        if (fileName != null) {
            buf.writeBoolean(true);
            ByteBufUtils.writeUTF8String(buf, this.fileName);
        } else {
            buf.writeBoolean(false);
        }
        if (filePath != null) {
            buf.writeBoolean(true);
            ByteBufUtils.writeUTF8String(buf, this.filePath);
        } else {
            buf.writeBoolean(false);
        }
        skin.requestId = new SkinIdentifier(skin);
        ByteBufHelper.writeSkinToByteBuf(buf, skin);
        buf.writeByte(sendType.ordinal());
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        if (buf.readBoolean()) {
            this.fileName = ByteBufUtils.readUTF8String(buf);
        }
        if (buf.readBoolean()) {
            this.filePath = ByteBufUtils.readUTF8String(buf);
        }
        this.skin = ByteBufHelper.readSkinFromByteBuf(buf);
        this.sendType = SendType.values()[buf.readByte()];
    }
    
    @Override
    public IMessage onMessage(MessageServerLibrarySendSkin message, MessageContext ctx) {
        ArmourersWorkshop.getProxy().receivedSkinFromLibrary(message.fileName, message.filePath, message.skin, message.sendType);
        return null;
    }
    
    public static enum SendType {
        LIBRARY_SAVE,
        GLOBAL_UPLOAD
    }
}
