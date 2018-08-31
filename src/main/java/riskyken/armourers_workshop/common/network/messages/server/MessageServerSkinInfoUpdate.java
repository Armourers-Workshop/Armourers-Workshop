package riskyken.armourers_workshop.common.network.messages.server;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.common.data.PlayerPointer;
import riskyken.armourers_workshop.common.skin.EntityEquipmentData;

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
        this.equipmentData = EntityEquipmentData.readFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.playerPointer.writeToByteBuffer(buf);
        EntityEquipmentData.writeToByteBuf(equipmentData, buf);
    }
    
    @Override
    public IMessage onMessage(MessageServerSkinInfoUpdate message, MessageContext ctx) {
        ArmourersWorkshop.proxy.addEquipmentData(message.playerPointer, message.equipmentData);
        return null;
    }
}
