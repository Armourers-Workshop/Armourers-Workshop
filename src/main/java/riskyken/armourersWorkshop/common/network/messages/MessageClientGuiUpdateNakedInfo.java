package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.equipment.EntityNakedInfo;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsPlayerEquipmentData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiUpdateNakedInfo implements IMessage, IMessageHandler<MessageClientGuiUpdateNakedInfo, IMessage> {

    EntityNakedInfo nakedInfo;
    
    public MessageClientGuiUpdateNakedInfo() {
        nakedInfo = new EntityNakedInfo();
    }

    public MessageClientGuiUpdateNakedInfo(EntityNakedInfo nakedInfo) {
        this.nakedInfo = nakedInfo;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nakedInfo.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        nakedInfo.toBytes(buf);
    }

    @Override
    public IMessage onMessage(MessageClientGuiUpdateNakedInfo message, MessageContext ctx) {
        ExtendedPropsPlayerEquipmentData customEquipmentData = ExtendedPropsPlayerEquipmentData.get(ctx.getServerHandler().playerEntity);
        customEquipmentData.setSkinInfo(message.nakedInfo);
        return null;
    }
}
