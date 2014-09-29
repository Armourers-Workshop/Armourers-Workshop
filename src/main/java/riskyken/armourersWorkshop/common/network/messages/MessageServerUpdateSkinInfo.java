package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;

import java.util.BitSet;
import java.util.UUID;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerUpdateSkinInfo implements IMessage, IMessageHandler<MessageServerUpdateSkinInfo, IMessage> {

    UUID playerId;
    boolean naked;
    int skinColour;
    int pantsColour;
    BitSet armourOverride;
    boolean headOverlay;
    
    public MessageServerUpdateSkinInfo() {}

    public MessageServerUpdateSkinInfo(UUID playerId, boolean naked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
        this.playerId = playerId;
        this.naked = naked;
        this.skinColour = skinColour;
        this.pantsColour = pantsColour;
        this.armourOverride = armourOverride;
        this.headOverlay = headOverlay;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = ByteBufHelper.readUUID(buf);
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
        ByteBufHelper.writeUUID(buf, this.playerId);
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
        ArmourersWorkshop.proxy.setPlayersNakedData(message.playerId, message.naked,
        		message.skinColour, message.pantsColour, message.armourOverride, message.headOverlay);
        return null;
    }
}
