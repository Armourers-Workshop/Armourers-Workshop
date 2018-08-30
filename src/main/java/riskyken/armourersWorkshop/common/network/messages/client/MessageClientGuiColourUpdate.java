package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;

public class MessageClientGuiColourUpdate implements IMessage, IMessageHandler<MessageClientGuiColourUpdate, IMessage> {

    int colour;
    boolean item;
    PaintType paintType;

    public MessageClientGuiColourUpdate() {
    }

    public MessageClientGuiColourUpdate(int colour, boolean item, PaintType paintType) {
        this.colour = colour;
        this.item = item;
        this.paintType = paintType;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.colour = buf.readInt();
        this.item = buf.readBoolean();
        this.paintType = PaintType.getPaintTypeFromUKey(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.colour);
        buf.writeBoolean(item);
        buf.writeInt(this.paintType.getKey());
    }

    @Override
    public IMessage onMessage(MessageClientGuiColourUpdate message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;

        if (player == null) {
            return null;
        }

        Container container = player.openContainer;

        if (container != null && container instanceof ContainerColourMixer) {
            TileEntityColourMixer colourMixer = ((ContainerColourMixer) container).getTileEntity();
            colourMixer.receiveColourUpdateMessage(message.colour, message.item, message.paintType);
        }

        return null;
    }
}
