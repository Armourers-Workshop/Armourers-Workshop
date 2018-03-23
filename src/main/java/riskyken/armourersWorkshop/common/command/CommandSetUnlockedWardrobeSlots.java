package riskyken.armourersWorkshop.common.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class CommandSetUnlockedWardrobeSlots extends ModCommand {

    @Override
    public String getCommandName() {
        return "setUnlockedWardrobeSlots";
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand.length == 2) {
            return getListOfStringsMatchingLastWord(currentCommand, getPlayers());
        }
        if (currentCommand.length == 3) {
            ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
            String[] skinTypesNames = new String[skinTypes.size()];
            for (int i = 0; i < skinTypes.size(); i++) {
                skinTypesNames[i] = skinTypes.get(i).getRegistryName();
            }
            return getListOfStringsMatchingLastWord(currentCommand, skinTypesNames);
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand.length != 4) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        String playerName = currentCommand[1];
        EntityPlayerMP player = getPlayer(commandSender, playerName);
        if (player == null) {
            return;
        }
        
        String skinTypeName = currentCommand[2];
        if (StringUtils.isNullOrEmpty(skinTypeName)) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        
        int count = 3;
        count = parseIntBounded(commandSender, currentCommand[3], 1, 8);

        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(skinTypeName);
        if (skinType == null) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        
        ExPropsPlayerSkinData.get(player).setSkinColumnCount(skinType, count);
    }
}
