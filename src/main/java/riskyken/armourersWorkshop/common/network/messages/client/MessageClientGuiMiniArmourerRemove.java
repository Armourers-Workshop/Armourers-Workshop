package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiMiniArmourerRemove implements IMessage, IMessageHandler<MessageClientGuiMiniArmourerRemove, IMessage> {
    
    private ISkinPartType skinPartType;
    private byte x, y, z;
    
    public MessageClientGuiMiniArmourerRemove() {
    }
    
    public MessageClientGuiMiniArmourerRemove(ISkinPartType skinPartType, byte x, byte y, byte z) {
        this.skinPartType = skinPartType;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        String partName = ByteBufUtils.readUTF8String(buf);
        this.skinPartType = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(partName);
        x = buf.readByte();
        y = buf.readByte();
        z = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, skinPartType.getRegistryName());
        buf.writeByte(x);
        buf.writeByte(y);
        buf.writeByte(z);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiMiniArmourerRemove message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) {
            return null;
        }
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerMiniArmourerBuilding) {
            ((ContainerMiniArmourerBuilding)container)
            .updateFromClientRemove(message.skinPartType, message.x, message.y, message.z);
        }
        return null;
    }
}
