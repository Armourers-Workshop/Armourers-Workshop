package riskyken.armourersWorkshop.common.network.messages;

import java.util.BitSet;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerUpdateSkinInfo implements IMessage, IMessageHandler<MessageServerUpdateSkinInfo, IMessage> {

    String playerName;
    boolean naked;
    int skinColour;
    int pantsColour;
    BitSet armourOverride;
    boolean headOverlay;
    
    public MessageServerUpdateSkinInfo() {}

    public MessageServerUpdateSkinInfo(String playerName, boolean naked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        this.playerName = playerName;
        this.naked = naked;
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
        this.armourOverride = armourOverride;
        this.headOverlay = headOverlay;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerName = ByteBufUtils.readUTF8String(buf);
        this.naked = buf.readBoolean();
        this.skinColour = buf.readInt();
        this.pantsColour = buf.readInt();
        this.armourOverride = new BitSet(4);
        for (int i = 0; i < 4; i++) {
        	this.armourOverride.set(i, buf.readBoolean());
        }
        this.headOverlay = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.playerName);
        buf.writeBoolean(this.naked);
        buf.writeInt(this.skinColour);
        buf.writeInt(this.pantsColour);
        for (int i = 0; i < 4; i++) {
        	buf.writeBoolean(this.armourOverride.get(i));
        }
        buf.writeBoolean(this.headOverlay);
    }

    @Override
    public IMessage onMessage(MessageServerUpdateSkinInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.setPlayersNakedData(message.playerName, message.naked,
        		message.skinColour, message.pantsColour, message.armourOverride, message.headOverlay);
        return null;
    }
}
