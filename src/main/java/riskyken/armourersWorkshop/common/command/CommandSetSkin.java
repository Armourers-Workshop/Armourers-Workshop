package riskyken.armourersWorkshop.common.command;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.common.library.LibraryFile;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinDye;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.ModLogger;
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
        
        int slotNum = 0;
        slotNum = parseIntBounded(commandSender, currentCommand[2], 1, 8);

        String skinName = currentCommand[3];
        if (!skinName.substring(0, 1).equals("\"")) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)skinName);
        }
        
        int usedCommands = 3;
        
        if (!skinName.substring(skinName.length() - 1, skinName.length()).equals("\"")) {
            for (int i = 4; i < currentCommand.length; i++) {
                skinName += " " + currentCommand[i];
                if (skinName.substring(skinName.length() - 1, skinName.length()).equals("\"")) {
                    usedCommands = i;
                    break;
                }
            }
        }        
        
        ModLogger.log("usedCommands used: " + usedCommands);
        ModLogger.log("total commands used: " + currentCommand.length);
        
        if (!skinName.substring(skinName.length() - 1, skinName.length()).equals("\"")) {
            throw new WrongUsageException(getCommandUsage(commandSender), (Object)skinName);
        }
        
        skinName = skinName.replace("\"", "");
        SkinDye skinDye = new SkinDye();
        
        for (int i = usedCommands + 1; i < currentCommand.length; i++) {
            String dyeCommand = currentCommand[i];
            ModLogger.log("Command dye: " + dyeCommand);
            
            if (!dyeCommand.contains("-")) {
                throw new WrongUsageException(getCommandUsage(commandSender), (Object)skinName);
            }
            String commandSplit[] = dyeCommand.split("-");
            if (commandSplit.length != 2) {
                throw new WrongUsageException(getCommandUsage(commandSender), (Object)skinName);
            }
            
            int dyeIndex = parseIntBounded(commandSender, commandSplit[0], 1, 8) - 1;
            String dye = commandSplit[1];
            
            if (dye.startsWith("#") && dye.length() == 7) {
                //dye = dye.substring(2, 8);
                if (isValidHex(dye)) {
                    Color dyeColour = Color.decode(dye);
                    int r = dyeColour.getRed();
                    int g = dyeColour.getGreen();
                    int b = dyeColour.getBlue();
                    skinDye.addDye(dyeIndex, new byte[] {(byte)r, (byte)g, (byte)b, (byte)255});
                } else {
                    throw new WrongUsageException("commands.armourers.invalidDyeFormat", (Object)dye);
                }
            } else if (dye.length() >= 5 & dye.contains(",")) {
                String dyeValues[] = dye.split(",");
                if (dyeValues.length != 3) {
                    throw new WrongUsageException(getCommandUsage(commandSender), (Object)skinName);
                }
                int r = parseIntBounded(commandSender, dyeValues[0], 0, 255);
                int g = parseIntBounded(commandSender, dyeValues[1], 0, 255);
                int b = parseIntBounded(commandSender, dyeValues[2], 0, 255);
                skinDye.addDye(dyeIndex, new byte[] {(byte)r, (byte)g, (byte)b, (byte)255});
            } else {
                throw new WrongUsageException("commands.armourers.invalidDyeFormat", (Object)dye);
            }
        }
        
        LibraryFile libraryFile = new LibraryFile(skinName);
        Skin skin = SkinIOUtils.loadSkinFromLibraryFile(libraryFile);
        if (skin == null) {
            throw new WrongUsageException("commands.armourers.fileNotFound", (Object)skinName);
        }
        try {
            skin.lightHash();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, String.format("Unable to create ID for file %s.", libraryFile.toString()));
            return;
        }
        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, libraryFile);
        SkinIdentifier skinIdentifier = new SkinIdentifier(0, libraryFile, 0, skin.getSkinType());
        ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinPointer(skinIdentifier, skinDye));
        ExPropsPlayerSkinData.get(player).setEquipmentStack(skinStack, slotNum - 1);
    }
    
    private boolean isValidHex (String colorStr) {
        ModLogger.log(colorStr);
        String hexPatten = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern pattern = Pattern.compile(hexPatten);
        Matcher matcher = pattern.matcher(colorStr);
        return matcher.matches();
    }
}
