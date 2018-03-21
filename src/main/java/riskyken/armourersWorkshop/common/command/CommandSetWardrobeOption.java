package riskyken.armourersWorkshop.common.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;

public class CommandSetWardrobeOption extends ModCommand {

    private static final String[] SUB_OPTIONS = new String[] {"showHeadArmour", "showChestArmour", "showLegArmour", "showFootArmour", "showHeadOverlay"};
    
    @Override
    public String getCommandName() {
        return "setWardrobeOption";
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand.length == 2) {
            return getListOfStringsMatchingLastWord(currentCommand, getPlayers());
        }
        if (currentCommand.length == 3) {
            return getListOfStringsMatchingLastWord(currentCommand, SUB_OPTIONS);
        }
        if (currentCommand.length == 4) {
            return getListOfStringsMatchingLastWord(currentCommand, new String[] {"true", "false"});
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand.length != 4) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        EntityPlayerMP player = getPlayer(commandSender, currentCommand[1]);
        if (player == null) {
            return;
        }
        
        String subOption = currentCommand[2];
        boolean value = parseBoolean(commandSender, currentCommand[3]);
        int subOptionIndex = -1;
        for (int i = 0; i < SUB_OPTIONS.length; i++) {
            if (subOption.equals(SUB_OPTIONS[i])) {
                subOptionIndex = i;
                break;
            }
        }
        if (subOptionIndex == -1) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        
        ExPropsPlayerEquipmentData playerEquipmentData = ExPropsPlayerEquipmentData.get(player);
        if (playerEquipmentData != null) {
            EquipmentWardrobeData ewd = playerEquipmentData.getEquipmentWardrobeData();
            if (subOptionIndex < 4) {
                ewd.armourOverride.set(subOptionIndex, !value);
            }
            if (subOptionIndex == 4) {
                ewd.headOverlay = !value;
            }
            playerEquipmentData.setSkinInfo(ewd, true);
        }
    }
}
