package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.customEquipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * @author RiskyKen
 *
 */
public class MessageServerAddEquipmentInfo implements IMessage, IMessageHandler<MessageServerAddEquipmentInfo, IMessage> {

    UUID playerId;
    EntityEquipmentData equipmentData;
    
    public MessageServerAddEquipmentInfo(UUID playerId, EntityEquipmentData equipmentData) {
        this.playerId = playerId;
        this.equipmentData = equipmentData;
    }
    
    public MessageServerAddEquipmentInfo() {}
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = ByteBufHelper.readUUID(buf);
        this.equipmentData = new EntityEquipmentData(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufHelper.writeUUID(buf, this.playerId);
        this.equipmentData.toBytes(buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerAddEquipmentInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.addEquipmentData(message.playerId, message.equipmentData);
        return null;
    }
}
