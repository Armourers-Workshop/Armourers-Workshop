package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;

public class MessageClientRequestSkinData implements IMessage, IMessageHandler<MessageClientRequestSkinData, IMessage> {

    int equpmentId;
    
    public MessageClientRequestSkinData() {}

    public MessageClientRequestSkinData(int equpmentId) {
        this.equpmentId = equpmentId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.equpmentId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.equpmentId);
    }

    @Override
    public IMessage onMessage(MessageClientRequestSkinData message, MessageContext ctx) {
        CommonSkinCache.INSTANCE.clientRequestEquipmentData(message.equpmentId, ctx.getServerHandler().playerEntity);
        return null;
    }

}
