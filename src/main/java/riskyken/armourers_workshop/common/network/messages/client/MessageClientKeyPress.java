package riskyken.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import riskyken.armourers_workshop.ArmourersWorkshop;
import riskyken.armourers_workshop.common.lib.LibGuiIds;
import riskyken.armourers_workshop.common.undo.UndoManager;

public class MessageClientKeyPress implements IMessage, IMessageHandler<MessageClientKeyPress, IMessage> {

    byte keyId;
    
    public MessageClientKeyPress() {
    }
    
    public MessageClientKeyPress(byte keyId) {
        this.keyId = keyId;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.keyId = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.keyId);
    }
    
    @Override
    public IMessage onMessage(MessageClientKeyPress message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        switch (message.keyId) {
        case 0:
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.CUSTOM_ARMOUR_INVENTORY, player.getEntityWorld(), 0, 0, 0);
            break;
        case 1:
            UndoManager.undoPressed(player);
            break;
        default:
            break;
        }
        
        return null;
    }
}
