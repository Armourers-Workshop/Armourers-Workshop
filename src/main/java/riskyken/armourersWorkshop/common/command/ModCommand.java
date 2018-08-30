package riskyken.armourersWorkshop.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.ArmourersWorkshop;

public abstract class ModCommand extends CommandBase {

    @Override
    public String getUsage(ICommandSender commandSender) {
        return "commands.armourers." + getName() + ".usage";
    }
    
    protected String[] getPlayers() {
        MinecraftServer server = ArmourersWorkshop.proxy.getServer();
        return server.getOnlinePlayerNames();
    }
}
