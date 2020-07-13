package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandWardrobeSetColour extends ModCommand {

    public CommandWardrobeSetColour(ModCommand parent) {
        super(parent, "set_colour");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        if (args.length == getParentCount() + 2) {
            ArrayList<String> types = new ArrayList<String>();
            for (ExtraColourType type : ExtraColourType.values()) {
                types.add(type.toString().toLowerCase());
            }
            return types;
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    // Arguments 3 - <player> <extra colour type> <dye>
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != getParentCount() + 3) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        String argPlayer = args[getParentCount()];
        String argColourType = args[getParentCount() + 1];
        String argDye = args[getParentCount() + 2];

        EntityPlayerMP player = getPlayer(server, sender, argPlayer);
        ExtraColourType colourType = ExtraColourType.valueOf(argColourType.toUpperCase());
        Color colour = null;

        if (argDye.startsWith("#") && argDye.length() == 7) {
            if (isValidHex(argDye)) {
                Color dyeColour = Color.decode(argDye);
                int r = dyeColour.getRed();
                int g = dyeColour.getGreen();
                int b = dyeColour.getBlue();
                colour = new Color(r, g, b, 255);
            } else {
                throw new WrongUsageException(getFullName() + ".invalidColourFormat", (Object) argDye);
            }
        } else if (argDye.length() >= 5 & argDye.contains(",")) {
            String dyeValues[] = argDye.split(",");
            if (dyeValues.length != 3) {
                throw new WrongUsageException(getUsage(sender), (Object) argDye);
            }
            int r = parseInt(dyeValues[0], 0, 255);
            int g = parseInt(dyeValues[1], 0, 255);
            int b = parseInt(dyeValues[2], 0, 255);
            colour = new Color(r, g, b, 255);
        } else {
            throw new WrongUsageException(getFullName() + ".invalidColourFormat", (Object) argDye);
        }

        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (wardrobeCap != null) {
            wardrobeCap.getExtraColours().setColour(colourType, colour.getRGB());
            wardrobeCap.syncToPlayer(player);
            wardrobeCap.syncToAllTracking();
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
