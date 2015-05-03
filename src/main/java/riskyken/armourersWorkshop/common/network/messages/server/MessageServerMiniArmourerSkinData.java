package riskyken.armourersWorkshop.common.network.messages.server;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.client.gui.GuiMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageServerMiniArmourerSkinData implements IMessage, IMessageHandler<MessageServerMiniArmourerSkinData, IMessage> {
    
    private ArrayList<SkinPart> skinParts;
    
    public MessageServerMiniArmourerSkinData() {
    }
    
    public MessageServerMiniArmourerSkinData(ArrayList<SkinPart> skinParts) {
        this.skinParts = skinParts;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readByte();
        skinParts = new ArrayList<SkinPart>();
        for (int i = 0; i < size; i++) {
            skinParts.add(new SkinPart(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(skinParts.size());
        for (int i = 0; i < skinParts.size(); i++) {
            skinParts.get(i).writeToBuf(buf);
        }
    }
    
    @Override
    public IMessage onMessage(MessageServerMiniArmourerSkinData message, MessageContext ctx) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        Minecraft mc = Minecraft.getMinecraft();
        
        GuiScreen screen = mc.currentScreen;
        
        if (screen != null && screen instanceof GuiMiniArmourerBuilding) {
            ((GuiMiniArmourerBuilding)screen).tileEntity.setSkinParts(message.skinParts);
        }
        
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerMiniArmourerBuilding) {
            ((ContainerMiniArmourerBuilding)container).setSkinParts(message.skinParts);
        }
        return null;
    }
}
