package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.PlayerEquipmentWardrobeData;

/**
 * Sent from the client to the server when a player
 * changes a value in their equipment wardrobe.
 * @author RiskyKen
 *
 */
public class MessageClientEquipmentWardrobeUpdate implements IMessage, IMessageHandler<MessageClientEquipmentWardrobeUpdate, IMessage> {

    PlayerEquipmentWardrobeData equipmentWardrobeData;
    
    public MessageClientEquipmentWardrobeUpdate() {
        equipmentWardrobeData = new PlayerEquipmentWardrobeData();
    }

    public MessageClientEquipmentWardrobeUpdate(PlayerEquipmentWardrobeData equipmentWardrobeData) {
        this.equipmentWardrobeData = equipmentWardrobeData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        equipmentWardrobeData.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        equipmentWardrobeData.toBytes(buf);
    }

    @Override
    public IMessage onMessage(MessageClientEquipmentWardrobeUpdate message, MessageContext ctx) {
        ExPropsPlayerEquipmentData customEquipmentData = ExPropsPlayerEquipmentData.get(ctx.getServerHandler().playerEntity);
        customEquipmentData.setSkinInfo(message.equipmentWardrobeData);
        return null;
    }
}
