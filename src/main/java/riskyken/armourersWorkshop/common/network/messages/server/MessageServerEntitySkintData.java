package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;

public class MessageServerEntitySkintData implements IMessage, IMessageHandler<MessageServerEntitySkintData, IMessage> {

    private EntityEquipmentData equipmentData;
    private int entityId;
    
    public MessageServerEntitySkintData() {
    }
    
    public MessageServerEntitySkintData(EntityEquipmentData equipmentData, int entityId) {
        this.equipmentData = equipmentData;
        this.entityId = entityId;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.equipmentData = new EntityEquipmentData(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        this.equipmentData.toBytes(buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerEntitySkintData message, MessageContext ctx) {
        ArmourersWorkshop.proxy.receivedEquipmentData(message.equipmentData, message.entityId);
        return null;
    }
}
