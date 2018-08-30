package riskyken.armourersWorkshop.common.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;

public class CommandAdminPanel extends ModCommand {

    @Override
    public String getName() {
        return "adminPanel";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] currentCommand) {
        EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
        if (player == null) {
            return;
        }
        MessageServerClientCommand message = new MessageServerClientCommand(CommandType.OPEN_ADMIN_PANEL);
        PacketHandler.networkWrapper.sendTo(message, player);
    }

}
