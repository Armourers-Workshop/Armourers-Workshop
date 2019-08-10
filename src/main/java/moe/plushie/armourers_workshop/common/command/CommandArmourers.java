package moe.plushie.armourers_workshop.common.command;

import net.minecraft.command.ICommandSender;

public class CommandArmourers extends ModSubCommands {

    public CommandArmourers() {
        super(null, "armourers");
        addSubCommand(new CommandClearModelCache(this));
        addSubCommand(new CommandClearSkin(this));
        addSubCommand(new CommandClearSkins(this));
        addSubCommand(new CommandGiveSkin(this));
        addSubCommand(new CommandResyncWardrobe(this));
        addSubCommand(new CommandSetSkin(this));
        addSubCommand(new CommandSetUnlockedWardrobeSlots(this));
        addSubCommand(new CommandAdminPanel(this));
        addSubCommand(new CommandSetItemAsSkinnable(this));
        addSubCommand(new CommandSetWardrobeOption(this));
        addSubCommand(new CommandExportSkin(this));
        addSubCommand(new CommandWardrobe(this));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(ICommandSender commandSender) {
        return "commands.armourers.usage";
    }
}
