package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.client.texture.PlayerTexture;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

public class MessageClientGuiSetSkin implements IMessage, IMessageHandler<MessageClientGuiSetSkin, IMessage> {

    PlayerTexture playerTexture;
    
    public MessageClientGuiSetSkin() {
    }
    
    public MessageClientGuiSetSkin(PlayerTexture playerTexture) {
        this.playerTexture = playerTexture;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound compound = new NBTTagCompound();
        playerTexture.writeToNBT(compound);
        ByteBufUtils.writeTag(buf, compound);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound compound = ByteBufUtils.readTag(buf);
        playerTexture = PlayerTexture.fromNBT(compound);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiSetSkin message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;
        if (container == null) { return null; }
        
        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourer armourerBrain = ((ContainerArmourer) container).getTileEntity();
            armourerBrain.setTexture(message.playerTexture);
        }
        return null;
    }
}
