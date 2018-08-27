package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.data.BipedRotations;
import riskyken.armourersWorkshop.common.inventory.ContainerMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiBipedRotations implements IMessage, IMessageHandler<MessageClientGuiBipedRotations, IMessage> {

    BipedRotations bipedRotations;
    
    public MessageClientGuiBipedRotations() {
        bipedRotations = new BipedRotations();
    }
    
    public MessageClientGuiBipedRotations(BipedRotations bipedRotations) {
        this.bipedRotations = bipedRotations;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        bipedRotations.readFromBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        bipedRotations.writeToBuf(buf);
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiBipedRotations message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;

        if (player == null) {
            return null;
        }

        Container container = player.openContainer;

        if (container != null && container instanceof ContainerMannequin) {
            TileEntityMannequin tileEntity = ((ContainerMannequin) container).getTileEntity();
            tileEntity.setBipedRotations(message.bipedRotations);
        }

        return null;
    }
}
