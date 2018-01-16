package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.client.model.bake.ModelBakery;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;
import riskyken.armourersWorkshop.common.skin.data.serialize.SkinIdentifierSerializer;

/**
 * Sent from server to client. Contains skin model information.
 * Clients will bake the model when they receive it. 
 * @author RiskyKen
 *
 */
public class MessageServerSendSkinData implements IMessage, IMessageHandler<MessageServerSendSkinData, IMessage> {

    private SkinIdentifier skinIdentifierRequested;
    private SkinIdentifier skinIdentifierUpdated;
    private Skin skin;
    
    public MessageServerSendSkinData() {}
    
    public MessageServerSendSkinData(SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated, Skin skin) {
        this.skinIdentifierRequested = skinIdentifierRequested;
        this.skinIdentifierUpdated = skinIdentifierUpdated;
        this.skin = skin;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        SkinIdentifierSerializer.writeToByteBuf(skinIdentifierRequested, buf);
        SkinIdentifierSerializer.writeToByteBuf(skinIdentifierUpdated, buf);
        buf.writeBoolean(skin != null);
        ByteBufHelper.writeSkinToByteBuf(buf, this.skin);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        Thread t = new Thread(new DownloadThread(buf), "Skin download thread.");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    @Override
    public IMessage onMessage(MessageServerSendSkinData message, MessageContext ctx) {
        return null;
    }
    
    public class DownloadThread implements Runnable {

        private ByteBuf buf;
        
        public DownloadThread(ByteBuf buf) {
            this.buf = buf;
        }
        
        @Override
        public void run() {
            SkinIdentifier skinIdentifierRequested = SkinIdentifierSerializer.readFromByteBuf(buf);
            SkinIdentifier skinIdentifierUpdated = SkinIdentifierSerializer.readFromByteBuf(buf);
            Skin skin = null;
            if (buf.readBoolean()) {
                skin = ByteBufHelper.readSkinFromByteBuf(buf);
            }
            sendSkinForBaking(skin, skinIdentifierRequested, skinIdentifierUpdated);
        }
        
        @SideOnly(Side.CLIENT)
        private void sendSkinForBaking(Skin skin, SkinIdentifier skinIdentifierRequested, SkinIdentifier skinIdentifierUpdated) {
            ModelBakery.INSTANCE.receivedUnbakedModel(skin, skinIdentifierRequested, skinIdentifierUpdated);
        }
    }
}
