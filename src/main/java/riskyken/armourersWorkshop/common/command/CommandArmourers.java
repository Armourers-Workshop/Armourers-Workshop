package riskyken.armourersWorkshop.common.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand.CommandType;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.utils.SkinIOUtils;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

public class CommandArmourers extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandName() {
        return "armourers";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "commands.armourers.usage";
    }
    
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        String[] commands = {"giveSkin", "clearSkins", "setSkin", "clearModelCache", "setSkinColumnCount"};
        
        switch (args.length) {
        case 1:
            return getListOfStringsMatchingLastWord(args, commands);
        case 2:
            return getListOfStringsMatchingLastWord(args, getPlayers(server));
        default:
            return null;
        }
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args == null) {
            throw new WrongUsageException("commands.armourers.usage", (Object)args);
        }
        if (args.length < 2) {
            throw new WrongUsageException("commands.armourers.usage", (Object)args);
        }
        String command = args[0];
        String playerName = args[1];
        EntityPlayerMP player = getPlayer(server, sender, playerName);
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
            Skin armourItemData = SkinIOUtils.loadSkinFromFileName(skinName + ".armour");
            if (armourItemData == null) {
                throw new WrongUsageException("commands.armourers.fileNotFound", (Object)skinName);
            }
            CommonSkinCache.INSTANCE.addEquipmentDataToCache(armourItemData, skinName);
            ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(armourItemData);
            EntityItem entityItem = player.dropItem(skinStack, false);
            entityItem.setNoPickupDelay();
            entityItem.setOwner(player.getName());
        } else if (command.equals("clearSkins")) {
            //ExPropsPlayerEquipmentData.get(player).clearAllEquipmentStacks();
        } else if (command.equals("setSkin")) {
            if (args.length < 3) {
                throw new WrongUsageException("commands.armourers.usage", (Object)args);
            } 
            String skinName = args[2];
            for (int i = 3; i < args.length; i++) {
                skinName += " " + args[i];
            }
            Skin armourItemData = SkinIOUtils.loadSkinFromFileName(skinName + ".armour");
            if (armourItemData == null) {
                throw new WrongUsageException("commands.armourers.fileNotFound", (Object)skinName);
            }
            CommonSkinCache.INSTANCE.addEquipmentDataToCache(armourItemData, skinName);
            ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(armourItemData);
            //ExPropsPlayerEquipmentData.get(player).setEquipmentStack(skinStack);
        } else if (command.equals("clearModelCache")) {
            PacketHandler.networkWrapper.sendTo(new MessageServerClientCommand(CommandType.CLEAR_MODEL_CACHE), player);
        } else if (command.equals("setSkinColumnCount")) {
            if (args.length < 3) {
                
                throw new WrongUsageException("commands.armourers.usage", (Object)args);
                
            }
            int count = 3;
            try {
                count = Integer.parseInt(args[2]);
            } catch (Exception e) {
                throw new WrongUsageException("commands.armourers.usage", (Object)args);
            }
            //ExPropsPlayerEquipmentData.get(player).setSkinColumnCount(count);
        } else {
            throw new WrongUsageException("commands.armourers.usage", (Object)args);
        }
    }
    
    private String[] getPlayers(MinecraftServer server) {
        return server.getAllUsernames();
    }
}
