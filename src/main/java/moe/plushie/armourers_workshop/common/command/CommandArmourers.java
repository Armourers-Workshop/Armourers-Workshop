package moe.plushie.armourers_workshop.common.command;

import moe.plushie.armourers_workshop.common.command.wardrobe.CommandWardrobe;
import net.minecraft.command.ICommandSender;

public class CommandArmourers extends ModSubCommands {

    public CommandArmourers() {
        super(null, "armourers");
        addSubCommand(new CommandClearModelCache(this));
        addSubCommand(new CommandGiveSkin(this));
        addSubCommand(new CommandAdminPanel(this));
        addSubCommand(new CommandSetItemAsSkinnable(this));
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
