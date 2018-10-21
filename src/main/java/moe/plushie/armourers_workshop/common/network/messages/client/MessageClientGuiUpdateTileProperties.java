package moe.plushie.armourers_workshop.common.network.messages.client;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.inventory.ContainerMannequin;
import moe.plushie.armourers_workshop.common.property.TileProperty;
import moe.plushie.armourers_workshop.common.property.TilePropertyManager;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiUpdateTileProperties implements IMessage, IMessageHandler<MessageClientGuiUpdateTileProperties, IMessage> {

    private ArrayList<TileProperty<?>> propertiesList;
    private NBTTagCompound compound;
    
    public MessageClientGuiUpdateTileProperties() {
    }
    
    public MessageClientGuiUpdateTileProperties(ArrayList<TileProperty<?>> propertiesList) {
        this.propertiesList = propertiesList;
    }
    
    public MessageClientGuiUpdateTileProperties(TileProperty<?>... properties) {
        propertiesList = new ArrayList<TileProperty<?>>();
        for (TileProperty<?> property : properties) {
            propertiesList.add(property);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound compound = new NBTTagCompound();
        for (TileProperty<?> property : propertiesList) {
            TilePropertyManager.INSTANCE.writePropToCompound(property, compound);
        }
        ByteBufUtils.writeTag(buf, compound);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        compound = ByteBufUtils.readTag(buf);
    }

    @Override
    public IMessage onMessage(MessageClientGuiUpdateTileProperties message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player == null) {
            return null;
        }
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerMannequin) {
            TileEntityMannequin tileEntity = ((ContainerMannequin)container).getTileEntity();
            tileEntity.disableSync();
            tileEntity.readPropsFromCompound(message.compound);
            tileEntity.enableSync();
            tileEntity.dirtySync();
        }
        return null;
    }
}
