package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.util.List;

import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandWardrobeSetOption extends ModCommand {

    private static final String[] SUB_OPTIONS = new String[] { "showFootArmour", "showLegArmour", "showChestArmour", "showHeadArmour" };

    public CommandWardrobeSetOption(ModCommand parent) {
        super(parent, "set_option");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        if (args.length == getParentCount() + 2) {
            return getListOfStringsMatchingLastWord(args, SUB_OPTIONS);
        }
        if (args.length == getParentCount() + 3) {
            return getListOfStringsMatchingLastWord(args, new String[] { "true", "false" });
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    // Arguments 3 - <player> <option> <value>
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != getParentCount() + 3) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        String argPlayer = args[getParentCount()];
        String argOption = args[getParentCount() + 1];
        boolean argValue = parseBoolean(args[getParentCount() + 2]);

        EntityPlayerMP player = getPlayer(server, sender, argPlayer);
        if (player == null) {
            return;
        }

        int subOptionIndex = -1;
        for (int i = 0; i < SUB_OPTIONS.length; i++) {
            if (argOption.equals(SUB_OPTIONS[i])) {
                subOptionIndex = i;
                break;
            }
        }
        if (subOptionIndex == -1) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (wardrobeCap != null) {
            if (subOptionIndex < 4) {
                EntityEquipmentSlot slot = EntityEquipmentSlot.values()[subOptionIndex + 2];
                wardrobeCap.setArmourOverride(slot, !argValue);
                wardrobeCap.syncToPlayer(player);
                wardrobeCap.syncToAllTracking();
            }
        }
    }
}
