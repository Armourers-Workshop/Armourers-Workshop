package riskyken.armourersWorkshop.common.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.common.equipment.ExtendedPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;

public class CommandArmourers extends CommandBase {

    @Override
    public String getCommandName() {
        return "armourers";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return null;
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] currentCommand) {
        String[] commands = {"giveSkin", "clearSkins", "setSkin"};
        
        switch (currentCommand.length) {
        case 1:
            return getListOfStringsMatchingLastWord(currentCommand, commands);
        case 2:
            return getListOfStringsMatchingLastWord(currentCommand, getPlayers());
        default:
            return null;
        }
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) {
        if (args != null && args.length > 1) {
            String command = args[0];
            String playerName = args[1];
            EntityPlayer player = getPlayer(commandSender, playerName);
            if (player != null) {
                switch (command) {
                case "giveSkin":
                    if (args.length > 2) {
                        String skinName = args[2];
                        for (int i = 3; i < args.length; i++) {
                            skinName += " " + args[i];
                        }
                        CustomEquipmentItemData armourItemData = TileEntityArmourLibrary.loadCustomArmourItemDataFromFile(skinName);
                        if (armourItemData == null) {
                            throw new WrongUsageException("commands.armourers.notfound", (Object)args);
                        }
                        ItemStack skinStack = EquipmentNBTHelper.makeStackForEquipment(armourItemData, false);
                        EntityItem entityItem = player.dropPlayerItemWithRandomChoice(skinStack, false);
                        entityItem.delayBeforeCanPickup = 0;
                        entityItem.func_145797_a(player.getCommandSenderName());
                    } else {
                        throw new WrongUsageException("commands.armourers.usage", (Object)args);
                    }
                    break;
                case "clearSkins":
                    ExtendedPropsPlayerEquipmentData.get(player).clearAllEquipmentStacks();
                    break;
                case "setSkin":
                    if (args.length > 2) {
                        String skinName = args[2];
                        for (int i = 3; i < args.length; i++) {
                            skinName += " " + args[i];
                        }
                        CustomEquipmentItemData armourItemData = TileEntityArmourLibrary.loadCustomArmourItemDataFromFile(skinName);
                        if (armourItemData == null) {
                            throw new WrongUsageException("commands.armourers.notfound", (Object)args);
                        }
                        ItemStack skinStack = EquipmentNBTHelper.makeStackForEquipment(armourItemData, false);
                        ExtendedPropsPlayerEquipmentData.get(player).setEquipmentStack(skinStack);
                    } else {
                        throw new WrongUsageException("commands.armourers.usage", (Object)args);
                    }
                    break;
                default:
                    throw new WrongUsageException("commands.armourers.usage", (Object)args);
                }
            } else {
                throw new WrongUsageException("commands.armourers.usage", (Object)args);
            }
        } else {
            throw new WrongUsageException("commands.armourers.usage", (Object)args);
        }
    }
    
    private String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
}
