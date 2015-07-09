package riskyken.armourersWorkshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * @author RiskyKen
 *
 */
public class MessageServerAddSkinInfo implements IMessage, IMessageHandler<MessageServerAddSkinInfo, IMessage> {

    PlayerPointer playerPointer;
    EntityEquipmentData equipmentData;
    
    public MessageServerAddSkinInfo(PlayerPointer playerPointer, EntityEquipmentData equipmentData) {
        this.playerPointer = playerPointer;
        this.equipmentData = equipmentData;
    }
    
    public MessageServerAddSkinInfo() {}
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerPointer = new PlayerPointer(buf);
        this.equipmentData = new EntityEquipmentData(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.playerPointer.writeToByteBuffer(buf);
        this.equipmentData.toBytes(buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerAddSkinInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.addEquipmentData(message.playerPointer, message.equipmentData);
        return null;
    }
}
