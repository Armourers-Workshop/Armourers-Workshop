package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class MessageClientGuiMiniArmourerCubeEdit implements IMessage, IMessageHandler<MessageClientGuiMiniArmourerCubeEdit, IMessage> {
    
    private ISkinPartType skinPartType;
    private ICube cube;
    private boolean remove;
    
    public MessageClientGuiMiniArmourerCubeEdit() {
    }
    
    public MessageClientGuiMiniArmourerCubeEdit(ISkinPartType skinPartType, ICube cube, boolean remove) {
        this.skinPartType = skinPartType;
        this.cube = cube;
        this.remove = remove;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        String partName = ByteBufUtils.readUTF8String(buf);
        this.skinPartType = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(partName);
        byte cubeId = buf.readByte();
        /*
        try {
            this.cube = CubeFactory.INSTANCE.getCubeInstanceFormId(cubeId);
            this.cube.readFromBuf(buf);
        } catch (InvalidCubeTypeException e) {
            e.printStackTrace();
        }
        */
        this.remove = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, skinPartType.getRegistryName());
        //this.cube.writeToBuf(buf);
        buf.writeBoolean(this.remove);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiMiniArmourerCubeEdit message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
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
