package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSendEquipmentData implements IMessage, IMessageHandler<MessageServerSendEquipmentData, IMessage> {

    CustomEquipmentItemData equipmentData;
    byte target;
    
    public MessageServerSendEquipmentData() {}
    
    public MessageServerSendEquipmentData(CustomEquipmentItemData equipmentData, byte target) {
        this.equipmentData = equipmentData;
        this.target = target;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.equipmentData = new CustomEquipmentItemData(buf);
        this.target = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.equipmentData.writeToBuf(buf);
        buf.writeByte(this.target);
    }

    @Override
    public IMessage onMessage(MessageServerSendEquipmentData message, MessageContext ctx) {
        ArmourersWorkshop.proxy.receivedEquipmentData(message.equipmentData, message.target);
        return null;
    }
}
