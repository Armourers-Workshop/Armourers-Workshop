package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiMiniArmourerAddOrEdit implements IMessage, IMessageHandler<MessageClientGuiMiniArmourerAddOrEdit, IMessage> {
    
    private ISkinPartType skinPartType;
    private ICube cube;
    
    public MessageClientGuiMiniArmourerAddOrEdit() {
    }
    
    public MessageClientGuiMiniArmourerAddOrEdit(ISkinPartType skinPartType, ICube cube) {
        this.skinPartType = skinPartType;
        this.cube = cube;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        String partName = ByteBufUtils.readUTF8String(buf);
        this.skinPartType = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(partName);
        byte cubeId = buf.readByte();
        
        try {
            this.cube = CubeRegistry.INSTANCE.getCubeInstanceFormId(cubeId);
            this.cube.readFromBuf(buf);
        } catch (InvalidCubeTypeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, skinPartType.getRegistryName());
        this.cube.writeToBuf(buf);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiMiniArmourerAddOrEdit message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) {
            return null;
        }
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerMiniArmourerBuilding) {
            ((ContainerMiniArmourerBuilding)container).updateFromClientAddOrEdit(message.skinPartType, message.cube);
        }
        return null;
    }
}
