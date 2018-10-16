package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.inventory.ContainerArmourer;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiSetArmourerSkinType implements IMessage, IMessageHandler<MessageClientGuiSetArmourerSkinType, IMessage> {

    private ISkinType skinType = null;
    
    public MessageClientGuiSetArmourerSkinType() {
    }
    
    public MessageClientGuiSetArmourerSkinType(ISkinType skinType) {
        this.skinType = skinType;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        String registryName = ByteBufUtils.readUTF8String(buf);
        this.skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(registryName);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        String registryName = "";
        if (this.skinType != null) {
            registryName = skinType.getRegistryName();
        }
        ByteBufUtils.writeUTF8String(buf, registryName);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiSetArmourerSkinType message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player == null) {
            return null;
        }
        Container container = player.openContainer;
        
        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourer te = ((ContainerArmourer)container).getTileEntity();
            te.setSkinType(message.skinType);
        }
        
        return null;
    }
}
