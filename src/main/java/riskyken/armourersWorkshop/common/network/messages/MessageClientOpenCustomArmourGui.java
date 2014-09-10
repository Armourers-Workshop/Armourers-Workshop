package riskyken.armourersWorkshop.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageClientOpenCustomArmourGui implements IMessage, IMessageHandler<MessageClientOpenCustomArmourGui, IMessage> {

    public MessageClientOpenCustomArmourGui() {
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {  
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }
    
    @Override
    public IMessage onMessage(MessageClientOpenCustomArmourGui message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.CUSTOM_ARMOUR_INVENTORY, player.worldObj, 0, 0, 0);
        return null;
    }
}
