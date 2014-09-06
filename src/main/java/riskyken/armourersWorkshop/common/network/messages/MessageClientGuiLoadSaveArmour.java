package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiLoadSaveArmour implements IMessage, IMessageHandler<MessageClientGuiLoadSaveArmour, IMessage> {

    String filename;
    boolean load;
    
    public MessageClientGuiLoadSaveArmour() { }
    
    public MessageClientGuiLoadSaveArmour(String filename, boolean load) {
        this.filename = filename;
        this.load = load;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.filename = ByteBufUtils.readUTF8String(buf);
        this.load = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.filename);
        buf.writeBoolean(this.load);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiLoadSaveArmour message,MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;
        
        if (container != null && container instanceof ContainerArmourLibrary) {
            TileEntityArmourLibrary te = ((ContainerArmourLibrary) container).getTileEntity();
            if (message.load) {
                te.loadArmour(message.filename, player);
            } else {
                te.saveArmour(message.filename, player);
            }
            
            ((ContainerArmourLibrary)container).sentList = false;
        }
        return null;
    }
}
