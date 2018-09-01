package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.network.ByteBufHelper;
import moe.plushie.armourers_workshop.common.network.SkinUploadHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Send from the client to the server to upload a skin in
 * multiple packets
 * @author RiskyKen
 *
 */
public class MessageClientSkinPart implements IMessage, IMessageHandler<MessageClientSkinPart, IMessage> {

    private int skinId;
    private byte packetId;
    private byte[] data;
    
    public MessageClientSkinPart() {
    }
    
    public MessageClientSkinPart(int skinId, byte packetId, byte[] data) {
        this.skinId = skinId;
        this.packetId = packetId;
        this.data = data;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.skinId = buf.readInt();
        this.packetId = buf.readByte();
        this.data = ByteBufHelper.readByteArrayFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.skinId);
        buf.writeByte(this.packetId);
        ByteBufHelper.writeByteArrayToByteBuf(buf, data);
    }
    
    @Override
    public IMessage onMessage(MessageClientSkinPart message, MessageContext ctx) {
        SkinUploadHelper.gotSkinPartFromClient(message.skinId, message.packetId, message.data, ctx.getServerHandler().player);
        return null;
    }
}
