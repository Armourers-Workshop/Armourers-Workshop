package moe.plushie.armourers_workshop.common.command.wardrobe;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.command.ModCommand;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandWardrobeClearSkin extends ModCommand {

    public CommandWardrobeClearSkin(ModCommand parent) {
        super(parent, "clear_skin");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == getParentCount() + 1) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        if (args.length == getParentCount() + 2) {
            ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
            String[] typeNames = new String[skinTypes.size()];
            for (int i = 0; i < skinTypes.size(); i++) {
                typeNames[i] = skinTypes.get(i).getRegistryName();
            }
            return getListOfStringsMatchingLastWord(args, typeNames);
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }

    // Arguments 3 - <player> <skin type> <slot id>
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != getParentCount() + 3) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        String argPlayer = args[getParentCount()];
        
        ModLogger.log(argPlayer);
        
        String argSkinType = args[getParentCount() + 1];
        int argSlotId = parseInt(args[getParentCount() + 2], 1, 10);

        EntityPlayerMP player = getPlayer(server, sender, argPlayer);
        if (player == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(argSkinType);
        if (skinType == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability != null) {
            skinCapability.clearSkin(skinType, argSlotId - 1);
            skinCapability.syncToPlayer(player);
            skinCapability.syncToAllTracking();
        }
    }
}
