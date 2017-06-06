package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import riskyken.armourersWorkshop.ArmourersWorkshop;

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
        ArmourersWorkshop.instance.proxy.receivedAdminPanelCommand(ctx.getServerHandler().playerEntity, message.command);
        return null;
    }
    
    public static enum AdminPanelCommand {
        RECOVER_SKINS,
        RELOAD_LIBRARY
    }
}
