package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerRemoveEquipmentInfo implements IMessage, IMessageHandler<MessageServerRemoveEquipmentInfo, IMessage> {

    UUID playerId;
    
    public MessageServerRemoveEquipmentInfo() {}
    
    public MessageServerRemoveEquipmentInfo(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = ByteBufHelper.readUUID(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufHelper.writeUUID(buf, this.playerId);
    }
    
    @Override
    public IMessage onMessage(MessageServerRemoveEquipmentInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.removeEquipmentData(message.playerId);
        return null;
    }
}
