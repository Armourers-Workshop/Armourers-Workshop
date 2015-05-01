package riskyken.armourersWorkshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        switch (message.keyId) {
        case 0:
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.CUSTOM_ARMOUR_INVENTORY, player.worldObj, 0, 0, 0);
            break;
        case 1:
            UndoManager.playerPressedUndo(player);
            break;
        default:
            break;
        }
        
        return null;
    }
}
