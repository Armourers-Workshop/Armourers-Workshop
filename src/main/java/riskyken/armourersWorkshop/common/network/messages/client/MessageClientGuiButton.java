package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

public class MessageClientGuiButton implements IMessage, IMessageHandler<MessageClientGuiButton, IMessage> {

    byte buttonId;
    
    public MessageClientGuiButton() {}
    
    public MessageClientGuiButton(byte buttonId) {
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buttonId = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(buttonId);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiButton message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;

        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourer armourerBrain = ((ContainerArmourer) container).getTileEntity();
            
            if (message.buttonId == 14) {
                armourerBrain.loadArmourItem(player);
            }
            if (message.buttonId == 7) {
                armourerBrain.toggleGuides();
            }
            if (message.buttonId == 9) {
                armourerBrain.toggleOverlay();
            }
            if (message.buttonId == 6) {
                armourerBrain.toggleHelper();
            }
            if (message.buttonId == 11) {
                //armourerBrain.cloneToSide(ForgeDirection.WEST);
            }
            if (message.buttonId == 12) {
                //armourerBrain.cloneToSide(ForgeDirection.EAST);
            }
        }
        
        if (container != null && container instanceof ContainerColourMixer) {
            TileEntityColourMixer colourMixer = ((ContainerColourMixer)container).getTileEntity();
            colourMixer.setColourFamily(ColourFamily.values()[message.buttonId]);
        }
        
        if (container instanceof IButtonPress) {
            ((IButtonPress)container).buttonPressed(message.buttonId);
        }
        
        return null;
    }
    
    public static interface IButtonPress {
        public void buttonPressed(byte buttonId);
    }
}
