package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSendEquipmentData implements IMessage, IMessageHandler<MessageServerSendEquipmentData, IMessage> {

    Skin equipmentData;
    
    public MessageServerSendEquipmentData() {}
    
    public MessageServerSendEquipmentData(Skin equipmentData) {
        this.equipmentData = equipmentData;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        Thread t = new Thread(new DownloadThread(buf), "Skin download thread.");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.equipmentData.writeToBuf(buf);
    }

    @Override
    public IMessage onMessage(MessageServerSendEquipmentData message, MessageContext ctx) {
        return null;
    }
    
    public class DownloadThread implements Runnable {

        private ByteBuf buf;
        
        public DownloadThread(ByteBuf buf) {
            this.buf = buf;
        }
        
        @Override
        public void run() {
            Skin skin = new Skin(buf);
            ArmourersWorkshop.proxy.receivedEquipmentData(skin);
        }
    }
}
