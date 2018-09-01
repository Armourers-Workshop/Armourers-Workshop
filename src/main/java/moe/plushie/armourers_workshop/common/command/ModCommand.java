package moe.plushie.armourers_workshop.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class ModCommand extends CommandBase {

    @Override
    public String getUsage(ICommandSender commandSender) {
        return "commands.armourers." + getName() + ".usage";
    }
    
    protected String[] getPlayers(MinecraftServer server) {
        return server.getOnlinePlayerNames();
    }
}
