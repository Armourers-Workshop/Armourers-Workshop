package riskyken.armourersWorkshop.common.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class CommandSetSkin extends ModCommand {

    @Override
    public String getCommandName() {
        return "setSkin";
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand.length == 2) {
            return getListOfStringsMatchingLastWord(currentCommand, getPlayers());
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] currentCommand) {
        if (currentCommand.length < 3) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)currentCommand);
        }
        
        
        String playerName = currentCommand[1];
        EntityPlayerMP player = getPlayer(commandSender, playerName);
        if (player == null) {
            return;
        }
        

        String skinName = currentCommand[2];
        for (int i = 3; i < currentCommand.length; i++) {
            skinName += " " + currentCommand[i];
        }
        Skin armourItemData = SkinIOUtils.loadSkinFromFileName(skinName + ".armour");
        if (armourItemData == null) {
            throw new WrongUsageException("commands.armourers.fileNotFound", (Object)skinName);
        }
        CommonSkinCache.INSTANCE.addEquipmentDataToCache(armourItemData, skinName);
        ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(armourItemData);
        ExPropsPlayerEquipmentData.get(player).setEquipmentStack(skinStack);
    }
}
