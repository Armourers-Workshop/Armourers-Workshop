package riskyken.armourersWorkshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerUpdateSkinInfo implements IMessage, IMessageHandler<MessageServerUpdateSkinInfo, IMessage> {

    PlayerPointer playerPointer;
    EntityNakedInfo nakedInfo;
    
    public MessageServerUpdateSkinInfo() {
        nakedInfo = new EntityNakedInfo();
    }

    public MessageServerUpdateSkinInfo(PlayerPointer playerPointer, EntityNakedInfo nakedInfo) {
        this.playerPointer = playerPointer;
        this.nakedInfo = nakedInfo;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerPointer = new PlayerPointer(buf);
        this.nakedInfo.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.playerPointer.writeToByteBuffer(buf);
        this.nakedInfo.toBytes(buf);
    }

    @Override
    public IMessage onMessage(MessageServerUpdateSkinInfo message, MessageContext ctx) {
        ArmourersWorkshop.proxy.setPlayersNakedData(message.playerPointer, message.nakedInfo);
        return null;
    }
}
