package riskyken.armourers_workshop.common.network.messages.server;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.common.skin.EntityEquipmentData;

public class MessageServerEntitySkinData implements IMessage, IMessageHandler<MessageServerEntitySkinData, IMessage> {

    private EntityEquipmentData equipmentData;
    private int entityId;
    
    public MessageServerEntitySkinData() {
    }
    
    public MessageServerEntitySkinData(EntityEquipmentData equipmentData, int entityId) {
        this.equipmentData = equipmentData;
        this.entityId = entityId;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.equipmentData = EntityEquipmentData.readFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        EntityEquipmentData.writeToByteBuf(this.equipmentData, buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerEntitySkinData message, MessageContext ctx) {
        ArmourersWorkshop.proxy.receivedEquipmentData(message.equipmentData, message.entityId);
        return null;
    }
}
