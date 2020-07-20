package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;

public class CommandWardrobeSetUnlockedSlots extends ModCommand {

    public CommandWardrobeSetUnlockedSlots(ModCommand parent) {
        super(parent, "set_unlocked_slots");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        if (args.length == getParentCount() + 2) {
            ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
            String[] skinTypesNames = new String[skinTypes.size()];
            for (int i = 0; i < skinTypes.size(); i++) {
                skinTypesNames[i] = skinTypes.get(i).getRegistryName();
            }
            return getListOfStringsMatchingLastWord(args, skinTypesNames);
        }
        if (args.length == getParentCount() + 3) {
            return getListOfStringsMatchingLastWord(args, new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" });
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }

    // Arguments 3 - <player> <skin type> <count>
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != getParentCount() + 3) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        String playerName = args[getParentCount()];
        EntityPlayerMP player = getPlayer(server, sender, playerName);
        if (player == null) {
            return;
        }

        String skinTypeName = args[getParentCount() + 1];
        if (StringUtils.isNullOrEmpty(skinTypeName)) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        int count = 3;
        count = parseInt(args[getParentCount() + 2], 0, EntitySkinCapability.MAX_SLOTS_PER_SKIN_TYPE);

        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(skinTypeName);
        if (skinType == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(player);
        if (wardrobeCap != null) {
            ModLogger.log("setting count " + count + " on " + skinType.getRegistryName());
            wardrobeCap.setUnlockedSlotsForSkinType(skinType, count);
            wardrobeCap.syncToPlayer(player);
            wardrobeCap.syncToAllTracking();
        }
    }
}
