package riskyken.armourersWorkshop.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class CommandArmourers extends CommandBase {
    
    private final ArrayList<ModCommand> subCommands;

    public CommandArmourers() {
        subCommands = new ArrayList<ModCommand>();
        subCommands.add(new CommandClearModelCache());
        subCommands.add(new CommandClearSkins());
        subCommands.add(new CommandGiveSkin());
        subCommands.add(new CommandResyncWardrobe());
        subCommands.add(new CommandSetSkin());
        subCommands.add(new CommandSetUnlockedWardrobeSlots());
        subCommands.add(new CommandAdminPanel());
        subCommands.add(new CommandSetItemAsSkinnable());
        subCommands.add(new CommandSetWardrobeOption());
        subCommands.add(new CommandExportSkin());
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getName() {
        return "armourers";
    }

    @Override
    public String getUsage(ICommandSender commandSender) {
        return "commands.armourers.usage";
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
    public List addTabCompletionOptions(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand.length == 1) {
            return getListOfStringsMatchingLastWord(currentCommand, getSubCommandNames());
        }
        if (currentCommand.length > 1) {
            String commandName = currentCommand[0];
            ModCommand command = getSubCommand(commandName);
            if (command != null) {
                return command.addTabCompletionOptions(commandSender, currentCommand);
            }
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand == null) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        if (currentCommand.length < 1) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        String commandName = currentCommand[0];
        ModCommand command = getSubCommand(commandName);
        if (command != null) {
            command.processCommand(commandSender, currentCommand);
            return;
        }
        throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
    }
    
    private String[] getPlayers() {
        return MinecraftServer.getServer().getOnlinePlayerNames();
    }
}
