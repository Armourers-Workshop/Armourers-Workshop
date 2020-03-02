package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.common.inventory.ContainerColourMixer;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourMixer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiColourUpdate implements IMessage, IMessageHandler<MessageClientGuiColourUpdate, IMessage> {

    int colour;
    boolean item;
    IPaintType paintType;

    public MessageClientGuiColourUpdate() {
    }

    public MessageClientGuiColourUpdate(int colour, boolean item, IPaintType paintType) {
        this.colour = colour;
        this.item = item;
        this.paintType = paintType;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.colour = buf.readInt();
        this.item = buf.readBoolean();
        this.paintType = PaintTypeRegistry.getInstance().getPaintTypeFromIndex(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.colour);
        buf.writeBoolean(item);
        buf.writeInt(this.paintType.getId());
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
