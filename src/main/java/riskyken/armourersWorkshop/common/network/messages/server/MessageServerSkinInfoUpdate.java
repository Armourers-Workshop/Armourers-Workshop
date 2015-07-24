package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentData;

/**
 * Send from the server to a client when a player walks into range
 * or they edit their skins.
 * @author RiskyKen
 *
 */
public class MessageServerSkinInfoUpdate implements IMessage, IMessageHandler<MessageServerSkinInfoUpdate, IMessage> {

    PlayerPointer playerPointer;
    EntityEquipmentData equipmentData;
    
    public MessageServerSkinInfoUpdate(PlayerPointer playerPointer, EntityEquipmentData equipmentData) {
        this.playerPointer = playerPointer;
        this.equipmentData = equipmentData;
    }
    
    public MessageServerSkinInfoUpdate() {}
    
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
    public IMessage onMessage(MessageServerSkinInfoUpdate message, MessageContext ctx) {
        ArmourersWorkshop.proxy.addEquipmentData(message.playerPointer, message.equipmentData);
        return null;
    }
}
