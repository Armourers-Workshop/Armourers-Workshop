package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.util.List;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandWardrobeClearSkins extends ModCommand {

    public CommandWardrobeClearSkins(ModCommand parent) {
        super(parent, "clear_skins");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    // Arguments 1 - <player>
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != getParentCount() + 1) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }
        
        String argPlayer = args[getParentCount()];
        EntityPlayerMP player = getPlayer(server, sender, argPlayer);
        if (player == null) {
            return;
        }
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability != null) {
            skinCapability.clear();
            skinCapability.syncToPlayer(player);
            skinCapability.syncToAllTracking();
        }
    }
}
