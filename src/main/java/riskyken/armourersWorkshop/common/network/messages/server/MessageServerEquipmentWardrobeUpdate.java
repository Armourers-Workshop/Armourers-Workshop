package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.PlayerEquipmentWardrobeData;

/**
 * Sent from the server to a client when a player walks
 * into tracking range or updates their equipment wardrobe.
 * @author RiskyKen
 *
 */
public class MessageServerEquipmentWardrobeUpdate implements IMessage, IMessageHandler<MessageServerEquipmentWardrobeUpdate, IMessage> {

    PlayerPointer playerPointer;
    PlayerEquipmentWardrobeData equipmentWardrobeData;
    
    public MessageServerEquipmentWardrobeUpdate() {
        equipmentWardrobeData = new PlayerEquipmentWardrobeData();
    }

    public MessageServerEquipmentWardrobeUpdate(PlayerPointer playerPointer, PlayerEquipmentWardrobeData equipmentWardrobeData) {
        this.playerPointer = playerPointer;
        this.equipmentWardrobeData = equipmentWardrobeData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerPointer = new PlayerPointer(buf);
        this.equipmentWardrobeData.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.playerPointer.writeToByteBuffer(buf);
        this.equipmentWardrobeData.toBytes(buf);
    }

    @Override
    public IMessage onMessage(MessageServerEquipmentWardrobeUpdate message, MessageContext ctx) {
        ArmourersWorkshop.proxy.setPlayersNakedData(message.playerPointer, message.equipmentWardrobeData);
        return null;
    }
}
