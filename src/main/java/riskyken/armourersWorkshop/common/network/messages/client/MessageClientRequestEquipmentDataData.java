package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientRequestEquipmentDataData implements IMessage, IMessageHandler<MessageClientRequestEquipmentDataData, IMessage> {

    int equpmentId;
    
    public MessageClientRequestEquipmentDataData() {}

    public MessageClientRequestEquipmentDataData(int equpmentId) {
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
    public IMessage onMessage(MessageClientRequestEquipmentDataData message, MessageContext ctx) {
        SkinDataCache.INSTANCE.clientRequestEquipmentData(message.equpmentId, ctx.getServerHandler().playerEntity);
        return null;
    }

}
