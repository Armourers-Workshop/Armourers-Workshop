package riskyken.armourersWorkshop.common.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.SkinDataCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.SkinIOUtils;

public class CommandArmourers extends CommandBase {

    @Override
    public String getCommandName() {
        return "armourers";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "commands.armourers.usage";
    }
    
    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] currentCommand) {
        String[] commands = {"giveSkin", "clearSkins", "setSkin", "clearModelCache"};
        
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
        if (args == null) {
            throw new WrongUsageException("commands.armourers.usage", (Object)args);
        }
        if (args.length < 2) {
            throw new WrongUsageException("commands.armourers.usage", (Object)args);
        }
        String command = args[0];
        String playerName = args[1];
        EntityPlayerMP player = getPlayer(commandSender, playerName);
        if (player == null) {
            return;
        }
        
        if (command.equals("giveSkin")) {
            if (args.length < 3) {
                throw new WrongUsageException("commands.armourers.usage", (Object)args);
            } 
            String skinName = args[2];
            for (int i = 3; i < args.length; i++) {
                skinName += " " + args[i];
            }
            skinName += ".armour";
            Skin armourItemData = SkinIOUtils.loadSkinFromFileName(skinName);
            if (armourItemData == null) {
                throw new WrongUsageException("commands.armourers.fileNotFound", (Object)skinName);
            }
            SkinDataCache.INSTANCE.addEquipmentDataToCache(armourItemData);
            ItemStack skinStack = EquipmentNBTHelper.makeEquipmentSkinStack(armourItemData);
            EntityItem entityItem = player.dropPlayerItemWithRandomChoice(skinStack, false);
            entityItem.delayBeforeCanPickup = 0;
            entityItem.func_145797_a(player.getCommandSenderName());
        } else if (command.equals("clearSkins")) {
            ExPropsPlayerEquipmentData.get(player).clearAllEquipmentStacks();
        } else if (command.equals("setSkin")) {
            if (args.length < 3) {
                throw new WrongUsageException("commands.armourers.usage", (Object)args);
            } 
            String skinName = args[2];
            for (int i = 3; i < args.length; i++) {
                skinName += " " + args[i];
            }
            skinName += ".armour";
            Skin armourItemData = SkinIOUtils.loadSkinFromFileName(skinName);
            if (armourItemData == null) {
                throw new WrongUsageException("commands.armourers.fileNotFound", (Object)skinName);
            }
            SkinDataCache.INSTANCE.addEquipmentDataToCache(armourItemData);
            ItemStack skinStack = EquipmentNBTHelper.makeEquipmentSkinStack(armourItemData);
            ExPropsPlayerEquipmentData.get(player).setEquipmentStack(skinStack);
        } else if (command.equals("clearModelCache")) {
            PacketHandler.networkWrapper.sendTo(new MessageServerClientCommand(CommandType.CLEAR_MODEL_CACHE), player);
        } else {
            throw new WrongUsageException("commands.armourers.usage", (Object)args);
        }
    }
    
    private String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }
}
