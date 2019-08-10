package moe.plushie.armourers_workshop.common.command;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandClearSkin extends ModCommand {

    public CommandClearSkin(ModCommand parent) {
        super(parent, "clearSkin");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        }
        if (args.length == 3) {
            ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
            String[] typeNames = new String[skinTypes.size()];
            for (int i = 0; i < skinTypes.size(); i++) {
                typeNames[i] = skinTypes.get(i).getRegistryName();
            }
            return getListOfStringsMatchingLastWord(args, typeNames);
        }

        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 4) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        String playerName = args[1];
        String skinTypeId = args[2];
        int slotId = parseInt(args[3], 1, 8);

        EntityPlayerMP player = getPlayer(server, sender, playerName);
        if (player == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(skinTypeId);
        if (skinType == null) {
            throw new WrongUsageException(getUsage(sender), (Object) args);
        }

        IEntitySkinCapability skinCapability = EntitySkinCapability.get(player);
        if (skinCapability != null) {
            skinCapability.clearSkin(skinType, slotId - 1);
            skinCapability.syncToPlayer(player);
            skinCapability.syncToAllTracking();
        }
    }
}
