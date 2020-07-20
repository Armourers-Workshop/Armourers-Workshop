package moe.plushie.armourers_workshop.common.command;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinIOUtils;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandGiveSkin extends ModCommand {

    public CommandGiveSkin(ModCommand parent) {
        super(parent, "give_skin");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }
        String playerName = args[1];
        EntityPlayerMP player = getPlayer(server, sender, playerName);
        if (player == null) {
            return;
        }

        String skinName = args[2];
        if (!skinName.substring(0, 1).equals("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object) skinName);
        }

        int usedCommands = 2;

        if (!skinName.substring(skinName.length() - 1, skinName.length()).equals("\"")) {
            for (int i = 3; i < args.length; i++) {
                skinName += " " + args[i];
                if (skinName.substring(skinName.length() - 1, skinName.length()).equals("\"")) {
                    usedCommands = i;
                    break;
                }
            }
        }

        ModLogger.log("usedCommands used: " + usedCommands);
        ModLogger.log("total commands used: " + args.length);

        if (!skinName.substring(skinName.length() - 1, skinName.length()).equals("\"")) {
            throw new WrongUsageException(getUsage(sender), (Object) skinName);
        }

        skinName = skinName.replace("\"", "");
        SkinDye skinDye = new SkinDye();

        for (int i = usedCommands + 1; i < args.length; i++) {
            String dyeCommand = args[i];
            ModLogger.log("Command dye: " + dyeCommand);

            if (!dyeCommand.contains("-")) {
                throw new WrongUsageException(getUsage(sender), (Object) skinName);
            }
            String commandSplit[] = dyeCommand.split("-");
            if (commandSplit.length < 2 | commandSplit.length > 3) {
                throw new WrongUsageException(getUsage(sender), (Object) skinName);
            }

            int dyeIndex = parseInt(commandSplit[0], 1, 8) - 1;
            String dye = commandSplit[1];
            IPaintType t = PaintTypeRegistry.PAINT_TYPE_NORMAL;
            if (commandSplit.length == 3) {
                String dyeString = commandSplit[2];
                t = PaintTypeRegistry.getInstance().getPaintTypeFormName(dyeString);
            }

            if (dye.startsWith("#") && dye.length() == 7) {
                if (isValidHex(dye)) {
                    Color dyeColour = Color.decode(dye);
                    int r = dyeColour.getRed();
                    int g = dyeColour.getGreen();
                    int b = dyeColour.getBlue();
                    skinDye.addDye(dyeIndex, new byte[] { (byte) r, (byte) g, (byte) b, (byte) t.getId() });
                } else {
                    throw new WrongUsageException("commands.armourers.invalidDyeFormat", (Object) dye);
                }
            } else if (dye.length() >= 5 & dye.contains(",")) {
                String dyeValues[] = dye.split(",");
                if (dyeValues.length != 3) {
                    throw new WrongUsageException(getUsage(sender), (Object) skinName);
                }
                int r = parseInt(dyeValues[0], 0, 255);
                int g = parseInt(dyeValues[1], 0, 255);
                int b = parseInt(dyeValues[2], 0, 255);
                skinDye.addDye(dyeIndex, new byte[] { (byte) r, (byte) g, (byte) b, (byte) t.getId() });
            } else {
                throw new WrongUsageException("commands.armourers.invalidDyeFormat", (Object) dye);
            }
        }

        LibraryFile libraryFile = new LibraryFile(skinName);
        Skin skin = SkinIOUtils.loadSkinFromLibraryFile(libraryFile);
        if (skin == null) {
            throw new WrongUsageException("commands.armourers.fileNotFound", (Object) skinName);
        }
        try {
            skin.lightHash();
        } catch (Exception e) {
            ModLogger.log(Level.ERROR, String.format("Unable to create ID for file %s.", libraryFile.toString()));
            return;
        }

        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, libraryFile);
        SkinIdentifier skinIdentifier = new SkinIdentifier(0, libraryFile, 0, skin.getSkinType());
        ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(skinIdentifier, skinDye));

        if (!player.inventory.addItemStackToInventory(skinStack)) {
            UtilItems.spawnItemAtEntity(player, skinStack, true);
        }
    }

    private boolean isValidHex(String colorStr) {
        ModLogger.log(colorStr);
        String hexPatten = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern pattern = Pattern.compile(hexPatten);
        Matcher matcher = pattern.matcher(colorStr);
        return matcher.matches();
    }
}
