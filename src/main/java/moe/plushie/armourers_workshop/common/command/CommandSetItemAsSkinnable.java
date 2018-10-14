package moe.plushie.armourers_workshop.common.command;

import java.util.List;

import moe.plushie.armourers_workshop.common.addons.ModAddon.ItemOverrideType;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;

public class CommandSetItemAsSkinnable extends ModCommand {

    @Override
    public String getName() {
        return "setItemAsSkinnable";
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
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 2) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if (player == null) {
            return;
        }
        ItemOverrideType type = ItemOverrideType.valueOf(args[1]);
        ItemStack stack = player.getHeldItemMainhand();
        if (!stack.isEmpty()) {
            Configuration config = ConfigHandler.config;
            ModAddonManager.addOverrideItem(type, stack.getItem());
        }
    }
}
