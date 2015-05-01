package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientLoadArmour implements IMessage, IMessageHandler<MessageClientLoadArmour, IMessage> {

    String name;
    String tags;
    
    public MessageClientLoadArmour() {}
    
    public MessageClientLoadArmour(String name, String tags) {
        this.name = name;
        this.tags = tags;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        name = ByteBufUtils.readUTF8String(buf);
        tags = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, tags);
    }
    
    @Override
    public IMessage onMessage(MessageClientLoadArmour message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;

        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourerBrain armourerBrain = ((ContainerArmourer) container).getTileEntity();
            
            armourerBrain.saveArmourItem(player, message.name, message.tags);
            
        }
        return null;
    }
}
