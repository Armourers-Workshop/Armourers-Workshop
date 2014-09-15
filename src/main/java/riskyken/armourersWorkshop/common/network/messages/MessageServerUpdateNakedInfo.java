package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerUpdateNakedInfo implements IMessage, IMessageHandler<MessageServerUpdateNakedInfo, IMessage> {

    UUID playerId;
    boolean naked;
    int skinColour;
    int pantsColour;
    
    public MessageServerUpdateNakedInfo() {}

    public MessageServerUpdateNakedInfo(UUID playerId, boolean naked, int skinColour, int pantsColour) {
        this.playerId = playerId;
        this.naked = naked;
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.naked = buf.readBoolean();
        this.skinColour = buf.readInt();
        this.pantsColour = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerId.toString());
        buf.writeBoolean(this.naked);
        buf.writeInt(this.skinColour);
        buf.writeInt(this.pantsColour);
    }

    @Override
    public IMessage onMessage(MessageServerUpdateNakedInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.setPlayersNakedData(message.playerId, message.naked, message.skinColour, message.pantsColour);
        return null;
    }
}
