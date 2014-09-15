package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.common.custom.equipment.PlayerCustomEquipmentData;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiUpdateNakedInfo implements IMessage, IMessageHandler<MessageClientGuiUpdateNakedInfo, IMessage> {

    boolean naked;
    int skinColour;
    int pantsColour;
    
    public MessageClientGuiUpdateNakedInfo() {}

    public MessageClientGuiUpdateNakedInfo(boolean naked, int skinColour, int pantsColour) {
        this.naked = naked;
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.naked = buf.readBoolean();
        this.skinColour = buf.readInt();
        this.pantsColour = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.naked);
        buf.writeInt(this.skinColour);
        buf.writeInt(this.pantsColour);
    }

    @Override
    public IMessage onMessage(MessageClientGuiUpdateNakedInfo message, MessageContext ctx) {
        PlayerCustomEquipmentData customEquipmentData = PlayerCustomEquipmentData.get(ctx.getServerHandler().playerEntity);
        customEquipmentData.setNakedInfo(message.naked, message.skinColour, message.pantsColour);
        return null;
    }
}
