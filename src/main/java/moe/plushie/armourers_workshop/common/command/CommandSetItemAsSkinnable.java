package moe.plushie.armourers_workshop.common.command;

import java.util.Arrays;
import java.util.List;

import moe.plushie.armourers_workshop.common.addons.ModAddonManager.ItemOverrideType;
import moe.plushie.armourers_workshop.common.config.ConfigHandlerOverrides;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSetItemAsSkinnable extends ModCommand {

    public CommandSetItemAsSkinnable(ModCommand parent) {
        super(parent, "set_item_skinnable");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            String[] values = new String[ItemOverrideType.values().length];
            for (int i = 0; i < values.length; i++) {
                values[i] = ItemOverrideType.values()[i].toString().toLowerCase();
            }
            return getListOfStringsMatchingLastWord(args, values);
        }
        if (args.length == 3) {
            String[] values = new String[] { "add", "remove" };
            return getListOfStringsMatchingLastWord(args, values);
        }
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 3) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if (player == null) {
            return;
        }
        ModLogger.log(Arrays.toString(args));
        ItemOverrideType type = null;
        try {
            type = ItemOverrideType.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }
        ItemStack stack = player.getHeldItemMainhand();
        if (!stack.isEmpty()) {
            if (args[2].equals("add")) {
                ConfigHandlerOverrides.addOverride(type, stack.getItem());
            } else if (args[2].equals("remove")) {
                ConfigHandlerOverrides.removeOverride(type, stack.getItem());
            } else {
                throw new WrongUsageException(getUsage(sender), (Object) args);
            }
        }
    }
}
