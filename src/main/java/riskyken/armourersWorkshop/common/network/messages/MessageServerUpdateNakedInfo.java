package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerUpdateNakedInfo implements IMessage, IMessageHandler<MessageServerUpdateNakedInfo, IMessage> {

    String playerName;
    boolean naked;
    int skinColour;
    int pantsColour;
    
    public MessageServerUpdateNakedInfo() {}

    public MessageServerUpdateNakedInfo(String playerName, boolean naked, int skinColour, int pantsColour) {
        this.playerName = playerName;
        this.naked = naked;
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerName = ByteBufUtils.readUTF8String(buf);
        this.naked = buf.readBoolean();
        this.skinColour = buf.readInt();
        this.pantsColour = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.playerName);
        buf.writeBoolean(this.naked);
        buf.writeInt(this.skinColour);
        buf.writeInt(this.pantsColour);
    }

    @Override
    public IMessage onMessage(MessageServerUpdateNakedInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.setPlayersNakedData(message.playerName, message.naked, message.skinColour, message.pantsColour);
        return null;
    }
}
