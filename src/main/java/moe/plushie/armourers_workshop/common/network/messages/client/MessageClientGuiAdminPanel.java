package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiAdminPanel implements IMessage, IMessageHandler<MessageClientGuiAdminPanel, IMessage> {

    private AdminPanelCommand command;
    
    public MessageClientGuiAdminPanel() {
    }
    
    public MessageClientGuiAdminPanel(AdminPanelCommand command) {
        this.command = command;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(command.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        command = AdminPanelCommand.values()[buf.readInt()];
    }

    @Override
    public IMessage onMessage(MessageClientGuiAdminPanel message, MessageContext ctx) {
        ArmourersWorkshop.getInstance().getProxy().receivedAdminPanelCommand(ctx.getServerHandler().player, message.command);
        return null;
    }
    
    public static enum AdminPanelCommand {
        RECOVER_SKINS,
        RELOAD_LIBRARY,
        UPDATE_SKINS,
        RELOAD_CACHE;
    }
}
