package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiSetArmourerCustomName implements IMessage, IMessageHandler<MessageClientGuiSetArmourerCustomName, IMessage> {

    String customName;

    public MessageClientGuiSetArmourerCustomName() {
    }
    
    public MessageClientGuiSetArmourerCustomName(String customName) {
        this.customName = customName;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.customName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.customName);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiSetArmourerCustomName message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;
        
        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourer te = ((ContainerArmourer)container).getTileEntity();
            te.setCustomName(message.customName);
        }
        return null;
    }
}
