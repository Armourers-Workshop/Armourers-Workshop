package riskyken.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import riskyken.armourers_workshop.common.skin.EquipmentWardrobeData;
import riskyken.armourers_workshop.common.skin.ExPropsPlayerSkinData;

/**
 * Sent from the client to the server when a player
 * changes a value in their skin wardrobe.
 * @author RiskyKen
 *
 */
public class MessageClientSkinWardrobeUpdate implements IMessage, IMessageHandler<MessageClientSkinWardrobeUpdate, IMessage> {

    EquipmentWardrobeData equipmentWardrobeData;
    
    public MessageClientSkinWardrobeUpdate() {
        equipmentWardrobeData = new EquipmentWardrobeData();
    }

    public MessageClientSkinWardrobeUpdate(EquipmentWardrobeData equipmentWardrobeData) {
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
    public IMessage onMessage(MessageClientSkinWardrobeUpdate message, MessageContext ctx) {
        ExPropsPlayerSkinData customEquipmentData = ExPropsPlayerSkinData.get(ctx.getServerHandler().player);
        customEquipmentData.setSkinInfo(message.equipmentWardrobeData, true);
        return null;
    }
}
