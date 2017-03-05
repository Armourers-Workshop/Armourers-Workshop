package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

public class MessageClientGuiSetArmourerSkinProps implements IMessage, IMessageHandler<MessageClientGuiSetArmourerSkinProps, IMessage> {

    SkinProperties skinProps;

    public MessageClientGuiSetArmourerSkinProps() {
    }
    
    public MessageClientGuiSetArmourerSkinProps(SkinProperties skinProps) {
        this.skinProps = skinProps;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound compound = ByteBufUtils.readTag(buf);
        skinProps = new SkinProperties();
        skinProps.readFromNBT(compound);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound compound = new NBTTagCompound();
        skinProps.writeToNBT(compound);
        ByteBufUtils.writeTag(buf, compound);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiSetArmourerSkinProps message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;
        
        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourer te = ((ContainerArmourer)container).getTileEntity();
            te.setSkinProps(message.skinProps);
        }
        return null;
    }
}
