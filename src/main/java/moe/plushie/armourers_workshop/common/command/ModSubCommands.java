package moe.plushie.armourers_workshop.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public abstract class ModSubCommands extends ModCommand {
    
    protected final ArrayList<ModCommand> subCommands;

    public ModSubCommands(ModCommand parent, String name) {
        super(parent, name);
        subCommands = new ArrayList<ModCommand>();
    }
    
    protected void addSubCommand(ModCommand modCommand) {
        subCommands.add(modCommand);
    }
    
    private String[] getSubCommandNames() {
        String[] subCommandNames = new String[subCommands.size()];
        for (int i = 0; i < subCommandNames.length; i++) {
            subCommandNames[i] = subCommands.get(i).getName();
        }
        Arrays.sort(subCommandNames);
        return subCommandNames;
    }
    
    private ModCommand getSubCommand(String name) {
        for (int i = 0; i < subCommands.size(); i++) {
            if (subCommands.get(i).getName().equals(name)) {
                return subCommands.get(i);
            }
        }
        return null;
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            return getListOfStringsMatchingLastWord(args, getSubCommandNames());
        }
        if (args.length > getParentCount() + 1) {
            String commandName = args[getParentCount()];
            ModCommand command = getSubCommand(commandName);
            if (command != null) {
                return command.getTabCompletions(server, sender, args, targetPos);
            }
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args == null) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        if (args.length < getParentCount() + 1) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        String commandName = args[getParentCount()];
        ModCommand command = getSubCommand(commandName);
        if (command != null) {
            command.execute(server, sender, args);
            return;
        }
        throw new WrongUsageException(getUsage(sender), (Object)args);
    }
}
