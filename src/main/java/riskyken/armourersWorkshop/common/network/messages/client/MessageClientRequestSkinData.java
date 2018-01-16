package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinIdentifier;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.serialize.SkinIdentifierSerializer;

public class MessageClientRequestSkinData implements IMessage, IMessageHandler<MessageClientRequestSkinData, IMessage> {

    private ISkinIdentifier skinIdentifier;
    
    public MessageClientRequestSkinData() {}

    public MessageClientRequestSkinData(ISkinIdentifier skinIdentifier) {
        this.skinIdentifier = skinIdentifier;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        SkinIdentifierSerializer.writeToByteBuf(skinIdentifier, buf);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        skinIdentifier = SkinIdentifierSerializer.readFromByteBuf(buf);
    }

    @Override
    public IMessage onMessage(MessageClientRequestSkinData message, MessageContext ctx) {
        CommonSkinCache.INSTANCE.clientRequestEquipmentData(message.skinIdentifier, ctx.getServerHandler().playerEntity);
        return null;
    }

}
