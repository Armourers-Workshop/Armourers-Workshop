package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerRemoveArmourData implements IMessage, IMessageHandler<MessageServerRemoveArmourData, IMessage> {

    String name;
    ArmourerType type;
    
    public MessageServerRemoveArmourData() {}
    
    public MessageServerRemoveArmourData(String name, ArmourerType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        name = ByteBufUtils.readUTF8String(buf);
        type = ArmourerType.getOrdinal(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeByte(type.ordinal());
    }
    
    @Override
    public IMessage onMessage(MessageServerRemoveArmourData message, MessageContext ctx) {
        ArmourersWorkshop.proxy.removeCustomArmour(message.name, message.type);
        return null;
    }
}
