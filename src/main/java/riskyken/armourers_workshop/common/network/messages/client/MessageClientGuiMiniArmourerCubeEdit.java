package riskyken.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import riskyken.armourers_workshop.api.common.skin.type.ISkinPartType;
import riskyken.armourers_workshop.common.data.MiniCube;
import riskyken.armourers_workshop.common.inventory.ContainerMiniArmourerBuilding;
import riskyken.armourers_workshop.common.skin.type.SkinTypeRegistry;

public class MessageClientGuiMiniArmourerCubeEdit implements IMessage, IMessageHandler<MessageClientGuiMiniArmourerCubeEdit, IMessage> {
    
    private ISkinPartType skinPartType;
    private riskyken.armourers_workshop.common.data.MiniCube cube;
    private boolean remove;
    
    public MessageClientGuiMiniArmourerCubeEdit() {
    }
    
    public MessageClientGuiMiniArmourerCubeEdit(ISkinPartType skinPartType, MiniCube cube, boolean remove) {
        this.skinPartType = skinPartType;
        this.cube = cube;
        this.remove = remove;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        String partName = ByteBufUtils.readUTF8String(buf);
        this.skinPartType = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(partName);
        byte cubeId = buf.readByte();
        this.cube = new MiniCube(buf);
        this.remove = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, skinPartType.getRegistryName());
        this.cube.writeToBuf(buf);
        buf.writeBoolean(this.remove);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiMiniArmourerCubeEdit message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player == null) {
            return null;
        }
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerMiniArmourerBuilding) {
            ((ContainerMiniArmourerBuilding)container).updateFromClientCubeEdit(message.skinPartType, message.cube, message.remove);
        }
        return null;
    }
}
