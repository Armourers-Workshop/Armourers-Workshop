package riskyken.armourersWorkshop.common.network.messages.server;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.network.ByteBufHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;

/**
 * Sent from server to client. Contains skin model information.
 * Clients will bake the model when they receive it. 
 * @author RiskyKen
 *
 */
public class MessageServerSendSkinData implements IMessage, IMessageHandler<MessageServerSendSkinData, IMessage> {

    Skin skin;
    
    public MessageServerSendSkinData() {}
    
    public MessageServerSendSkinData(Skin skin) {
        this.skin = skin;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        Thread t = new Thread(new DownloadThread(buf), "Skin download thread.");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufHelper.writeSkinToByteBuf(buf, this.skin);
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
            Skin skin = ByteBufHelper.readSkinFromByteBuf(buf);
            ArmourersWorkshop.proxy.receivedEquipmentData(skin);
        }
    }
}
