package moe.plushie.armourers_workshop.common.command;

import java.util.List;

import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;

public class CommandClearCache extends ModCommand {

    private static final String[] CACHE_SIDE = { "client", "server" };

    public CommandClearCache(ModCommand parent) {
        super(parent, "clear_cache");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            return getListOfStringsMatchingLastWord(args, CACHE_SIDE);
        }

        if (args.length == getParentCount() + 2) {
            if (args[getParentCount()].equalsIgnoreCase("client")) {
                return getListOfStringsMatchingLastWord(args, getPlayers(server));
            }
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    // Arguments 3 - <cache size> [player is client side]
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= getParentCount()) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        Side side = Side.valueOf(args[getParentCount()].toUpperCase());

        switch (side) {
        case CLIENT:
            if (args.length != getParentCount() + 2) {
                throw new WrongUsageException(getUsage(sender), (Object) args);
            }
            String playerName = args[getParentCount() + 1];
            EntityPlayerMP player = getPlayer(server, sender, playerName);
            if (player == null) {
                return;
            }
            PacketHandler.networkWrapper.sendTo(new MessageServerClientCommand(CommandType.CLEAR_MODEL_CACHE), player);
            break;
        case SERVER:
            if (args.length != getParentCount() + 1) {
                throw new WrongUsageException(getUsage(sender), (Object) args);
            }
            CommonSkinCache.INSTANCE.clearAll();
            break;
        }
    }
}
