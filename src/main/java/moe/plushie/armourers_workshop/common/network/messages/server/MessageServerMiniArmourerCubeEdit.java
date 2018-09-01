package moe.plushie.armourers_workshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.client.gui.miniarmourer.GuiMiniArmourerBuilding;
import moe.plushie.armourers_workshop.common.data.MiniCube;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerMiniArmourerCubeEdit implements IMessage, IMessageHandler<MessageServerMiniArmourerCubeEdit, IMessage> {

    private ISkinPartType skinPartType;
    private moe.plushie.armourers_workshop.common.data.MiniCube cube;
    private boolean remove;

    public MessageServerMiniArmourerCubeEdit() {
    }
    
    public MessageServerMiniArmourerCubeEdit(ISkinPartType skinPartType, MiniCube cube, boolean remove) {
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
    public IMessage onMessage(MessageServerMiniArmourerCubeEdit message, MessageContext ctx) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
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
