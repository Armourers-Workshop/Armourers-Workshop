package moe.plushie.armourers_workshop.common.command;

import java.util.List;

import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSetWardrobeOption extends ModCommand {

    private static final String[] SUB_OPTIONS = new String[] {"showFootArmour", "showLegArmour", "showChestArmour", "showHeadArmour"};
    
    @Override
    public String getName() {
        return "setWardrobeOption";
    }
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        if (args.length == 3) {
            return getListOfStringsMatchingLastWord(args, SUB_OPTIONS);
        }
        if (args.length == 4) {
            return getListOfStringsMatchingLastWord(args, new String[] {"true", "false"});
        }
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 4) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        EntityPlayerMP player = getPlayer(server, sender, args[1]);
        if (player == null) {
            return;
        }
        
        String subOption = args[2];
        boolean value = parseBoolean(args[3]);
        int subOptionIndex = -1;
        for (int i = 0; i < SUB_OPTIONS.length; i++) {
            if (subOption.equals(SUB_OPTIONS[i])) {
                subOptionIndex = i;
                break;
            }
        }
        if (subOptionIndex == -1) {
            throw new WrongUsageException(getUsage(sender), (Object)args);
        }
        
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (wardrobeCap != null) {
            if (subOptionIndex < 4) {
                EntityEquipmentSlot slot = EntityEquipmentSlot.values()[subOptionIndex + 2];
                wardrobeCap.setArmourOverride(slot, !value);
                wardrobeCap.syncToPlayer(player);
                wardrobeCap.syncToAllTracking();
            }
        }
    }
}
