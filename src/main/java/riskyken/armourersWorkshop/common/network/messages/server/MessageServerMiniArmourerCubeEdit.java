package riskyken.armourersWorkshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.gui.GuiMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerMiniArmourerCubeEdit implements IMessage, IMessageHandler<MessageServerMiniArmourerCubeEdit, IMessage> {

    private ISkinPartType skinPartType;
    private ICube cube;
    private boolean remove;

    public MessageServerMiniArmourerCubeEdit() {
    }
    
    public MessageServerMiniArmourerCubeEdit(ISkinPartType skinPartType, ICube cube, boolean remove) {
        this.skinPartType = skinPartType;
        this.cube = cube;
        this.remove = remove;
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
        this.remove = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, skinPartType.getRegistryName());
        this.cube.writeToBuf(buf);
        buf.writeBoolean(this.remove);
    }
    
    @Override
    public IMessage onMessage(MessageServerMiniArmourerCubeEdit message, MessageContext ctx) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        Minecraft mc = Minecraft.getMinecraft();
        if (player == null) {
            return null;
        }
        
        GuiScreen screen = mc.currentScreen;
        if (screen != null && screen instanceof GuiMiniArmourerBuilding) {
            ((GuiMiniArmourerBuilding)screen).tileEntity.cubeUpdateFromServer(message.skinPartType, message.cube, message.remove);
        }
        return null;
    }
}
