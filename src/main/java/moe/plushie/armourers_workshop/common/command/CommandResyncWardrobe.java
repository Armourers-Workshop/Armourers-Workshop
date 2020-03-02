package moe.plushie.armourers_workshop.common.command;

import java.util.List;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandResyncWardrobe extends ModCommand {

    public CommandResyncWardrobe(ModCommand parent) {
        super(parent, "resyncWardrobe");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }
        String playerName = args[1];
        EntityPlayerMP player = getPlayer(server, sender, playerName);
        if (player == null) {
            return;
        }
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (skinCapability != null) {
            skinCapability.syncToPlayer(player);
            skinCapability.syncToAllTracking();
        }
        if (wardrobeCap != null) {
            wardrobeCap.syncToPlayer(player);
            wardrobeCap.syncToAllTracking();
        }
    }
}
