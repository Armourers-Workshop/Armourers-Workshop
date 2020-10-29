package moe.plushie.armourers_workshop.common.command;

import moe.plushie.armourers_workshop.common.command.CommandExecute.ICommandExecute;
import moe.plushie.armourers_workshop.common.command.wardrobe.CommandWardrobe;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandArmourers extends ModSubCommands {

    public CommandArmourers() {
        super(null, "armourers");
        addSubCommand(new CommandAdminPanel(this));
        addSubCommand(new CommandClearCache(this));
        addSubCommand(new CommandExportSkin(this));
        addSubCommand(new CommandGiveSkin(this));
        addSubCommand(new CommandExecute(this, "open_folder", new ICommandExecute() {

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                EntityPlayerMP player = getCommandSenderAsPlayer(sender);
                PacketHandler.networkWrapper.sendTo(new MessageServerClientCommand(CommandType.OPEN_MOD_FOLDER), player);
            }
        }));
        addSubCommand(new CommandSetItemAsSkinnable(this));

        addSubCommand(new CommandWardrobe(this));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
