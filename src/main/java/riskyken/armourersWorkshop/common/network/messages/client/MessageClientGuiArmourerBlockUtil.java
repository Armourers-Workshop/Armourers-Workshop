package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;

public class MessageClientGuiArmourerBlockUtil implements IMessage, IMessageHandler<MessageClientGuiArmourerBlockUtil, IMessage> {
    
    private String utilType;
    private ISkinPartType partType1;
    private ISkinPartType partType2;
    
    public MessageClientGuiArmourerBlockUtil() {
    }
    
    public MessageClientGuiArmourerBlockUtil(String utilType, ISkinPartType part1, ISkinPartType part2) {
        this.utilType = utilType;
        this.partType1 = part1;
        this.partType2 = part2;
    }


    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, utilType);
        buf.writeBoolean(partType1 != null);
        if (partType1 != null) {
            ByteBufUtils.writeUTF8String(buf, partType1.getRegistryName());
        }
        buf.writeBoolean(partType2 != null);
        if (partType2 != null) {
            ByteBufUtils.writeUTF8String(buf, partType2.getRegistryName());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        utilType = ByteBufUtils.readUTF8String(buf);
        if (buf.readBoolean()) {
            String registryName = ByteBufUtils.readUTF8String(buf);
            partType1 = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(registryName);
        }
        if (buf.readBoolean()) {
            String registryName = ByteBufUtils.readUTF8String(buf);
            partType2 = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(registryName);
        }
    }


    @Override
    public IMessage onMessage(MessageClientGuiArmourerBlockUtil message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null){
            return null;
        }
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourer armourerBrain = ((ContainerArmourer) container).getTileEntity();
            
            if (message.utilType.equals("clear")) {
                armourerBrain.clearArmourCubes(message.partType1);
            }
        }
        return null;
    }
}
