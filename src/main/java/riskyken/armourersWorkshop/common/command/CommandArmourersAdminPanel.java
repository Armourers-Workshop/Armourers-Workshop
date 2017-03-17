package riskyken.armourersWorkshop.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;

public class CommandArmourersAdminPanel extends CommandBase {
    
    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }
    
    @Override
    public String getCommandName() {
        return "armourers-admin-panel";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "commands.armourersAdminPanel.usage";
    }
    
    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
        if (player == null) {
            return;
        }
        MessageServerClientCommand message = new MessageServerClientCommand(CommandType.OPEN_ADMIN_PANEL);
        PacketHandler.networkWrapper.sendTo(message, player);
    }
}
