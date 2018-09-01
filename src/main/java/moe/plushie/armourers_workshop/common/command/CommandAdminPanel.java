package moe.plushie.armourers_workshop.common.command;

import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

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
