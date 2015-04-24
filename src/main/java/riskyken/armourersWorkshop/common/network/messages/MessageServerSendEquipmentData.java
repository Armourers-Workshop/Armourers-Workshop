package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSendEquipmentData implements IMessage, IMessageHandler<MessageServerSendEquipmentData, IMessage> {

    Skin equipmentData;
    
    public MessageServerSendEquipmentData() {}
    
    public MessageServerSendEquipmentData(Skin equipmentData) {
        this.equipmentData = equipmentData;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.equipmentData = new Skin(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.equipmentData.writeToBuf(buf);
    }

    @Override
    public IMessage onMessage(MessageServerSendEquipmentData message, MessageContext ctx) {
        ArmourersWorkshop.proxy.receivedEquipmentData(message.equipmentData);
        return null;
    }
}
