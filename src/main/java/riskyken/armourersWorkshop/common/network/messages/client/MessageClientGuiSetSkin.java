package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiSetSkin implements IMessage, IMessageHandler<MessageClientGuiSetSkin, IMessage> {

    String username;
    
    public MessageClientGuiSetSkin() {
        // TODO Auto-generated constructor stub
    }
    
    public MessageClientGuiSetSkin(String username) {
        this.username = username;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        username = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, username);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiSetSkin message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;
        if (container == null) { return null; }
        
        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourer armourerBrain = ((ContainerArmourer) container).getTileEntity();
            GameProfile gameProfile = new GameProfile(null, message.username);
            armourerBrain.setGameProfile(gameProfile);
        }
        
        return null;
    }
}
