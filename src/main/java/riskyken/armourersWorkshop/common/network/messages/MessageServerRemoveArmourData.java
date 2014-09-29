package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerRemoveArmourData implements IMessage, IMessageHandler<MessageServerRemoveArmourData, IMessage> {

    UUID playerId;
    ArmourType type;
    
    public MessageServerRemoveArmourData() {}
    
    public MessageServerRemoveArmourData(UUID playerId, ArmourType type) {
        this.playerId = playerId;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = ByteBufHelper.readUUID(buf);
        this.type = ArmourType.getOrdinal(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufHelper.writeUUID(buf, this.playerId);
        buf.writeByte(type.ordinal());
    }
    
    @Override
    public IMessage onMessage(MessageServerRemoveArmourData message, MessageContext ctx) {
        ArmourersWorkshop.proxy.removeCustomArmour(message.playerId, message.type);
        return null;
    }
}
