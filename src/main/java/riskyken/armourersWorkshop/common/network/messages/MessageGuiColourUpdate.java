package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageGuiColourUpdate implements IMessage, IMessageHandler<MessageGuiColourUpdate, IMessage> {

    int colour;

    public MessageGuiColourUpdate() {
    }

    public MessageGuiColourUpdate(int colour) {
        this.colour = colour;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.colour = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.colour);
    }

    @Override
    public IMessage onMessage(MessageGuiColourUpdate message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;

        if (player == null) {
            return null;
        }

        Container container = player.openContainer;

        if (container != null && container instanceof ContainerColourMixer) {
            TileEntityColourMixer colourMixer = ((ContainerColourMixer) container).getTileEntity();
            colourMixer.receiveColourUpdateMessage(message.colour);
        }

        return null;
    }
}
