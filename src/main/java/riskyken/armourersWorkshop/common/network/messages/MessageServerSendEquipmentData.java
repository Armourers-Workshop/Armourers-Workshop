package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.equipment.data.CustomArmourItemData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSendEquipmentData implements IMessage, IMessageHandler<MessageServerSendEquipmentData, IMessage> {

    CustomArmourItemData equipmentData;
    byte target;
    
    public MessageServerSendEquipmentData() {}
    
    public MessageServerSendEquipmentData(CustomArmourItemData equipmentData, byte target) {
        this.equipmentData = equipmentData;
        this.target = target;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.equipmentData = new CustomArmourItemData(buf);
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
