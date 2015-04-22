package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerUpdateSkinInfo implements IMessage, IMessageHandler<MessageServerUpdateSkinInfo, IMessage> {

    UUID playerId;
    EntityNakedInfo nakedInfo;
    
    public MessageServerUpdateSkinInfo() {
        nakedInfo = new EntityNakedInfo();
    }

    public MessageServerUpdateSkinInfo(UUID playerId, EntityNakedInfo nakedInfo) {
        this.playerId = playerId;
        this.nakedInfo = nakedInfo;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerId = ByteBufHelper.readUUID(buf);
        this.nakedInfo.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufHelper.writeUUID(buf, this.playerId);
        this.nakedInfo.toBytes(buf);
    }

    @Override
    public IMessage onMessage(MessageServerUpdateSkinInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.setPlayersNakedData(message.playerId, message.nakedInfo);
        return null;
    }
}
