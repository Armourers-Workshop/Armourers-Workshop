package riskyken.armourersWorkshop.common.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.proxies.ClientProxy;

public class CommandCustomArmour extends CommandBase {

    @Override
    public String getCommandName() {
        return "customarmour";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "commands.customarmour.usage";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
    
    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        EntityPlayer player = commandSender.getEntityWorld().getPlayerEntityByName(commandSender.getCommandSenderName());
        
        if (args == null | player == null) {
            throw new WrongUsageException("commands.playerhead.usage", (Object)args);
        }
        
        if (args.length != 1) {
            throw new WrongUsageException("commands.playerhead.usage", (Object)args);
        }
        
        if (args[0].equalsIgnoreCase("clear")) {
            ClientProxy.RemoveAllCustomArmourData(player);
            return;
        }
        
        throw new WrongUsageException("commands.playerhead.usage", (Object)args);
    }

}
