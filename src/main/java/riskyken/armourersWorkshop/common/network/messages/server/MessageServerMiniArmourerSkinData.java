package riskyken.armourersWorkshop.common.network.messages.server;

import java.util.ArrayList;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.miniarmourer.GuiMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;

/**
 * Sent from the server to the client when a player opens the mini armourer GUI.
 * Contains all the block data for the model being built.
 * @author RiskyKen
 *
 */
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
            //skinParts.add(new SkinPart(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(skinParts.size());
        for (int i = 0; i < skinParts.size(); i++) {
            //skinParts.get(i).writeToBuf(buf);
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
        return null;
    }
}
