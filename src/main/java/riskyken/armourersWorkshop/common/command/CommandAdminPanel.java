package riskyken.armourersWorkshop.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;

public class CommandAdminPanel extends ModCommand {

    @Override
    public String getName() {
        return "adminPanel";
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if (player == null) {
            return;
        }
        MessageServerClientCommand message = new MessageServerClientCommand(CommandType.OPEN_ADMIN_PANEL);
        PacketHandler.networkWrapper.sendTo(message, player);
    }
}
