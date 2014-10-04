package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.custom.equipment.EquipmentDataCache;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientRequestEquipmentDataData implements IMessage, IMessageHandler<MessageClientRequestEquipmentDataData, IMessage> {

    int equpmentId;
    byte target;
    
    public MessageClientRequestEquipmentDataData() {}

    public MessageClientRequestEquipmentDataData(int equpmentId, byte target) {
        this.equpmentId = equpmentId;
        this.target = target;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.equpmentId = buf.readInt();
        this.target = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.equpmentId);
        buf.writeByte(this.target);
    }

    @Override
    public IMessage onMessage(MessageClientRequestEquipmentDataData message, MessageContext ctx) {
        EquipmentDataCache.clientRequestEquipmentData(message.equpmentId, message.target, ctx.getServerHandler().playerEntity);
        return null;
    }

}
