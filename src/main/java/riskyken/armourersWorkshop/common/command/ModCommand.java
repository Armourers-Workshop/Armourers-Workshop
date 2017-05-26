package riskyken.armourersWorkshop.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.ArmourersWorkshop;

public abstract class ModCommand extends CommandBase {

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "commands.armourers." + getCommandName() + ".usage";
    }
    
    protected String[] getPlayers() {
        MinecraftServer server = ArmourersWorkshop.proxy.getServer();
        return server.getAllUsernames();
    }
}
